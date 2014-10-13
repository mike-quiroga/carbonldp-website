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

public class BooleanMessageConverter implements HttpMessageConverter<Boolean> {

	protected final Log logger = LogFactory.getLog(getClass());

	private List<MediaType> supportedMediaTypes = Collections.emptyList();

	public BooleanMessageConverter() {
		//@formatter:off
		setSupportedMediaTypes(Arrays.asList(
			new MediaType("text", "plain"),
			new MediaType("application", "json")
		));
		//@formatter:on
	}

	protected boolean supports(Class<?> clazz) {
		return Boolean.class.isAssignableFrom(clazz);
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
	public Boolean read(Class<? extends Boolean> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
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
	public void write(Boolean value, MediaType mediaType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
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

		writeBoolean(value, mediaType, outputStream);

		// Set the Content-Length
		headers.add(com.base22.carbon.HTTPHeaders.CONTENT_LENGTH, String.valueOf(outputStream.size()));
		outputStream.writeTo(outputMessage.getBody());

		outputMessage.getBody().flush();
	}

	private void writeBoolean(Boolean value, MediaType contentType, OutputStream outputStream) throws IOException {
		String booleanString = String.valueOf(value);
		outputStream.write(booleanString.getBytes());
	}
}