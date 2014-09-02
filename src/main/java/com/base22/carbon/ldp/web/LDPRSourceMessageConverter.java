package com.base22.carbon.ldp.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.base22.carbon.HttpUtil;
import com.base22.carbon.ldp.LDPRSource;

public class LDPRSourceMessageConverter implements HttpMessageConverter<LDPRSource> {

	protected final Log logger = LogFactory.getLog(getClass());

	private List<Lang> supportedLanguages = Collections.emptyList();

	// The languages are added in order of preferance. The default language goes first.
	public LDPRSourceMessageConverter() {
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
		boolean supports = LDPRSource.class.isAssignableFrom(clazz);
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
		return false;
	};

	@Override
	public LDPRSource read(Class<? extends LDPRSource> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
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
	public void write(LDPRSource ldpRSource, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();

		if ( headers.getContentType() == null ) {
			if ( contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype() ) {
				contentType = contentTypeToMediaType(getDefaultLanguage().getContentType());
			}
			if ( contentType != null ) {
				headers.setContentType(contentType);
			}
		}

		addLocationHeader(headers, ldpRSource);
		addLinkTypeHeaders(headers, ldpRSource);
		addETagHeader(headers, ldpRSource);

		// TODO: Optimize the way Content-Length is set
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		writeLDPRSource(ldpRSource, contentType, outputStream);

		// Set the Content-Length
		headers.add(com.base22.carbon.HttpHeaders.CONTENT_LENGTH, String.valueOf(outputStream.size()));
		outputStream.writeTo(outputMessage.getBody());

		outputMessage.getBody().flush();
	}

	private void writeLDPRSource(LDPRSource ldpRSource, MediaType contentType, OutputStream outputStream) throws IOException {
		Lang languageToUse = null;
		for (Lang language : supportedLanguages) {
			if ( contentType.isCompatibleWith(contentTypeToMediaType(language.getContentType())) ) {
				languageToUse = language;
			}
		}
		ldpRSource.getResource().getModel().write(outputStream, languageToUse.getName());
	}

	private void addLocationHeader(HttpHeaders headers, LDPRSource ldpRSource) {
		headers.add(com.base22.carbon.HttpHeaders.LOCATION, ldpRSource.getURI());
	}

	private void addLinkTypeHeaders(HttpHeaders headers, LDPRSource ldpRSource) {
		for (String types : ldpRSource.getLinkTypes()) {
			headers.add(com.base22.carbon.HttpHeaders.LINK, types);
		}
	}

	private void addETagHeader(HttpHeaders headers, LDPRSource ldpRSource) {
		if ( ldpRSource.getETag() != null ) {
			headers.add(com.base22.carbon.HttpHeaders.ETAG, HttpUtil.formatWeakETag(ldpRSource.getETag()));
		}
	}

	// TODO: Move this to another place
	private MediaType contentTypeToMediaType(ContentType contentType) {
		return new MediaType(contentType.getType(), contentType.getSubType());
	}
}
