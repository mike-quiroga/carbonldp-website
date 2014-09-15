package com.base22.carbon.sparql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.base22.carbon.ldp.RDFUtil;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class ResultSetMessageConverter implements HttpMessageConverter<ResultSet> {

	protected final Log logger = LogFactory.getLog(getClass());

	private List<MediaType> supportedMediaTypes = Collections.emptyList();

	private static enum Language {
		//@formatter:off
		JSON("application", "sparql-results+json"),
		XML("application", "sparql-results+xml"),
		CSV("text", "csv"),
		TSV("text", "tab-separated-values");
		//@formatter:on

		private final MediaType mediaType;
		private final MediaType genericMediaType;

		Language(String type, String subtype) {
			this.mediaType = new MediaType(type, subtype);

			if ( subtype.contains("+") ) {
				subtype = subtype.split("\\+")[1];
				this.genericMediaType = new MediaType(type, subtype);
			} else {
				this.genericMediaType = null;
			}
		}

		public MediaType getMediaType() {
			return mediaType;
		}

		public MediaType getGenericMediaType() {
			return genericMediaType;
		}

		public static List<MediaType> getSupportedMediaTypes() {
			List<MediaType> mediaTypes = new ArrayList<MediaType>();
			for (Language language : Language.values()) {
				mediaTypes.add(language.getMediaType());
				if ( language.getGenericMediaType() != null ) {
					mediaTypes.add(language.getGenericMediaType());
				}
			}
			return mediaTypes;
		}
	}

	public ResultSetMessageConverter() {
		setSupportedMediaTypes(Language.getSupportedMediaTypes());
	}

	protected boolean supports(Class<?> clazz) {
		boolean supports = ResultSet.class.isAssignableFrom(clazz);
		return supports;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		Assert.notEmpty(supportedMediaTypes, "'supportedMediaTypes' must not be empty");
		this.supportedMediaTypes = new ArrayList<MediaType>(supportedMediaTypes);
	}

	protected MediaType getDefaultMediaType() {
		return (! supportedMediaTypes.isEmpty() ? supportedMediaTypes.get(0) : null);
	}

	@Override
	public boolean canRead(java.lang.Class<?> clazz, MediaType mediaType) {
		// return supports(clazz) && canRead(mediaType);
		return false;
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
	public ResultSet read(Class<? extends ResultSet> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
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
	public void write(ResultSet resultSet, MediaType mediaType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();

		if ( headers.getContentType() == null ) {
			if ( mediaType == null || mediaType.isWildcardType() || mediaType.isWildcardSubtype() ) {
				mediaType = getDefaultMediaType();
			}
			if ( mediaType != null ) {
				headers.setContentType(mediaType);
			}
		}

		// TODO: Optimize the way Content-Length is set
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		writeResultSet(resultSet, mediaType, outputStream);

		// Set the Content-Length
		headers.add(com.base22.carbon.HttpHeaders.CONTENT_LENGTH, String.valueOf(outputStream.size()));
		outputStream.writeTo(outputMessage.getBody());

		outputMessage.getBody().flush();
	}

	private void writeResultSet(ResultSet resultSet, MediaType contentType, OutputStream outputStream) throws IOException {
		Language languageToUse = null;
		for (Language language : Language.values()) {
			if ( contentType.isCompatibleWith(language.getMediaType()) ) {
				languageToUse = language;
				break;
			}
			if ( language.getGenericMediaType() != null ) {
				if ( contentType.isCompatibleWith(language.getGenericMediaType()) ) {
					languageToUse = language;
					break;
				}
			}
		}

		switch (languageToUse) {
			case CSV:
				ResultSetFormatter.outputAsCSV(outputStream, resultSet);
				break;
			case JSON:
				ResultSetFormatter.outputAsJSON(outputStream, resultSet);
				break;
			case TSV:
				ResultSetFormatter.outputAsTSV(outputStream, resultSet);
				break;
			case XML:
				ResultSetFormatter.outputAsXML(outputStream, resultSet);
				break;
			default:
				break;
		}
	}
}
