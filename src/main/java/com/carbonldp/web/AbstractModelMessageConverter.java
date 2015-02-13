package com.carbonldp.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.commons.utils.MediaTypeUtil;

public class AbstractModelMessageConverter implements HttpMessageConverter<AbstractModel> {

	private List<RDFFormat> supportedFormats = Collections.emptyList();
	private List<MediaType> supportedMediaTypes = Collections.emptyList();
	private Map<MediaType, RDFFormat> mediaTypeFormats;

	private ConfigurationRepository configurationRepository;

	public AbstractModelMessageConverter(ConfigurationRepository configurationRepository) {
		Assert.notNull(configurationRepository);

		//@formatter:off
		setSupportedFormats(
			Arrays.asList(
				RDFFormat.TURTLE,
				RDFFormat.JSONLD,
				RDFFormat.RDFJSON,
				RDFFormat.RDFXML
			)
		);
		//@formatter:on

		this.configurationRepository = configurationRepository;
	}

	private boolean supports(Class<?> clazz) {
		return AbstractModel.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canRead(mediaType);
	}

	private boolean canRead(MediaType mediaType) {
		if ( mediaType == null || MediaType.ALL.equals(mediaType) ) return true;

		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if ( supportedMediaType.isCompatibleWith(mediaType) ) return true;
		}

		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canWrite(mediaType);
	}

	private boolean canWrite(MediaType mediaType) {
		if ( mediaType == null || MediaType.ALL.equals(mediaType) ) return true;

		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if ( supportedMediaType.isCompatibleWith(mediaType) ) return true;
		}

		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	@Override
	public AbstractModel read(Class<? extends AbstractModel> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mediaType = inputMessage.getHeaders().getContentType();

		RDFFormat formatToUse;
		if ( this.mediaTypeFormats.containsKey(mediaType) ) formatToUse = this.mediaTypeFormats.get(mediaType);
		else formatToUse = this.getDefaultFormat();

		InputStream bodyInputStream = inputMessage.getBody();

		RDFParser parser = Rio.createParser(formatToUse);
		AbstractModel model = new LinkedHashModel();
		String baseURI = configurationRepository.forgeGenericRequestURL();

		parser.setRDFHandler(new StatementCollector(model));

		try {
			parser.parse(bodyInputStream, baseURI);
		} catch (RDFParseException | RDFHandlerException e) {
			throw new HttpMessageNotReadableException("The message couldn't be parsed into an RDF Model.", e);
		}

		return model;
	}

	@Override
	public void write(AbstractModel model, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();

		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = MediaTypeUtil.fromString(this.getDefaultFormat().getDefaultMIMEType());
			}
			if ( contentType != null ) {
				headers.setContentType(contentType);
			}
		}

		RDFFormat formatToUse = this.mediaTypeFormats.get(contentType);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			writeModel(model, formatToUse, outputStream);
		} catch (RDFHandlerException e) {
			throw new HttpMessageNotWritableException("The RDF model couldn't be wrote to an RDF document.", e);
		}

		// TODO: Set the Content-Length

		outputStream.writeTo(outputMessage.getBody());

		outputMessage.getBody().flush();
	}

	private void writeModel(AbstractModel model, RDFFormat format, OutputStream outputStream) throws RDFHandlerException {
		RDFWriter writer = Rio.createWriter(format, outputStream);
		writer.startRDF();
		for (Statement statement : model) {
			writer.handleStatement(statement);
		}
		writer.endRDF();
	}

	private void setSupportedFormats(List<RDFFormat> supportedFormats) {
		Assert.notEmpty(supportedFormats, "'supportedFormats' must not be empty");

		this.supportedMediaTypes = new ArrayList<MediaType>();
		this.mediaTypeFormats = new HashMap<MediaType, RDFFormat>();

		for (RDFFormat format : supportedFormats) {
			List<MediaType> mediaTypes = MediaTypeUtil.fromStrings(format.getMIMETypes());
			for (MediaType mediaType : mediaTypes) {
				this.mediaTypeFormats.put(mediaType, format);
			}
			this.supportedMediaTypes.addAll(mediaTypes);
		}

		this.supportedFormats = supportedFormats;
	}

	private RDFFormat getDefaultFormat() {
		return (! this.supportedFormats.isEmpty() ? this.supportedFormats.get(0) : null);
	}

}
