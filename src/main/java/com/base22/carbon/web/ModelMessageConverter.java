package com.base22.carbon.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RiotException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.base22.carbon.ldp.RDFUtil;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

public class ModelMessageConverter implements HttpMessageConverter<Model> {

	protected final Log logger = LogFactory.getLog(getClass());

	private List<Lang> supportedLanguages = Collections.emptyList();

	// The languages are added in order of preferance. The default language goes first.
	public ModelMessageConverter() {
		//@formatter:off
		setSupportedLanguages(
			Arrays.asList(
				Lang.TURTLE,
				Lang.JSONLD,
				Lang.RDFJSON,
				Lang.RDFXML
			)
		);
		//@formatter:on
	}

	protected boolean supports(Class<?> clazz) {
		boolean supports = Model.class.isAssignableFrom(clazz);
		return supports;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		for (Lang language : supportedLanguages) {
			MediaType mediaType = new MediaType(language.getContentType().getType(), language.getContentType().getSubType());
			mediaTypes.add(mediaType);
		}
		return mediaTypes;
	}

	public void setSupportedLanguages(List<Lang> supportedLanguages) {
		Assert.notEmpty(supportedLanguages, "'supportedLanguages' must not be empty");
		this.supportedLanguages = new ArrayList<Lang>(supportedLanguages);
	}

	protected Lang getDefaultLanguage() {
		return (! supportedLanguages.isEmpty() ? supportedLanguages.get(0) : null);
	}

	@Override
	public boolean canRead(java.lang.Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canRead(mediaType);
	};

	protected boolean canRead(MediaType mediaType) {
		if ( mediaType == null || MediaType.ALL.equals(mediaType) ) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if ( supportedMediaType.isCompatibleWith(mediaType) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Model read(Class<? extends Model> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mediaType = inputMessage.getHeaders().getContentType();
		Lang languageToUse = null;
		for (Lang language : supportedLanguages) {
			if ( mediaType.isCompatibleWith(contentTypeToMediaType(language.getContentType())) ) {
				languageToUse = language;
			}
		}
		if ( languageToUse == null ) {
			languageToUse = getDefaultLanguage();
		}

		InputStream bodyInputStream = inputMessage.getBody();

		bodyInputStream = addDefaultPrefixes(bodyInputStream, languageToUse);

		String genericRequestURI = HTTPUtil.createGenericRequestURI();

		Model model = null;
		try {
			model = RDFUtil.createInMemoryModel(bodyInputStream, languageToUse, genericRequestURI);
		} catch (RiotException e) {
			throw new HttpMessageNotReadableException("The entityBody of the request couldn't be parsed into an RDF Model.", e);
		}
		return model;
	}

	protected InputStream addDefaultPrefixes(InputStream bodyInputStream, Lang language) throws IOException {
		return RDFUtil.setDefaultNSPrefixes(bodyInputStream, language, true);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return supports(clazz) && canWrite(mediaType);
	}

	protected boolean canWrite(MediaType mediaType) {
		if ( mediaType == null || MediaType.ALL.equals(mediaType) ) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if ( supportedMediaType.isCompatibleWith(mediaType) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void write(Model model, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();

		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = contentTypeToMediaType(getDefaultLanguage().getContentType());
			}
			if ( contentType != null ) {
				headers.setContentType(contentType);
			}
		}

		// TODO: Optimize the way Content-Length is set
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		writeModel(model, contentType, outputStream);

		// Set the Content-Length
		headers.add(com.base22.carbon.HTTPHeaders.CONTENT_LENGTH, String.valueOf(outputStream.size()));
		outputStream.writeTo(outputMessage.getBody());

		outputMessage.getBody().flush();
	}

	private void writeModel(Model model, MediaType contentType, OutputStream outputStream) throws IOException {
		Lang languageToUse = null;
		for (Lang language : supportedLanguages) {
			if ( contentType.isCompatibleWith(contentTypeToMediaType(language.getContentType())) ) {
				languageToUse = language;
			}
		}
		model.write(outputStream, languageToUse.getName());
	}

	// TODO: Move this to another place
	private MediaType contentTypeToMediaType(ContentType contentType) {
		return new MediaType(contentType.getType(), contentType.getSubType());
	}
}
