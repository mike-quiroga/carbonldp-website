package com.base22.carbon.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.base22.carbon.constants.LDPNR;
import com.base22.carbon.constants.LDPR;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.models.WrapperForLDPNR;
import com.base22.carbon.utils.HttpUtil;

public class WrapperForLDPNRMessageConverter implements HttpMessageConverter<WrapperForLDPNR> {

	protected final Log logger = LogFactory.getLog(getClass());

	public WrapperForLDPNRMessageConverter() {

	}

	protected boolean supports(Class<?> clazz) {
		return WrapperForLDPNR.class.isAssignableFrom(clazz);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(MediaType.ALL);
	}

	@Override
	public boolean canRead(java.lang.Class<?> clazz, MediaType mediaType) {
		return false;
	};

	@Override
	public WrapperForLDPNR read(Class<? extends WrapperForLDPNR> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return supports(clazz);
	}

	@Override
	public void write(WrapperForLDPNR wrapper, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

		if ( wrapper.getFileInputStream() == null ) {
			// The wrapper will be sent as an LDPRSource
			LDPResourceMessageConverter converter = new LDPResourceMessageConverter();
			converter.write(wrapper, contentType, outputMessage);
			return;
		}

		HttpHeaders headers = outputMessage.getHeaders();

		addLocationHeader(headers, wrapper);
		addContentTypeHeader(headers, wrapper);
		addLinkTypeHeaders(headers, wrapper);
		addETagHeader(headers, wrapper);
		addDescribedByHeader(headers, wrapper);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		writeFile(wrapper, contentType, outputStream);

		// Set the Content-Length
		headers.add(com.base22.carbon.constants.HttpHeaders.CONTENT_LENGTH, String.valueOf(outputStream.size()));
		outputStream.writeTo(outputMessage.getBody());
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO: FT
		}

		outputMessage.getBody().flush();
	}

	private void writeFile(WrapperForLDPNR wrapper, MediaType contentType, OutputStream outputStream) throws IOException {
		IOUtils.copy(wrapper.getFileInputStream(), outputStream);
		try {
			wrapper.getFileInputStream().close();
		} catch (IOException e) {
			// TODO: FT
		}
	}

	private void addLocationHeader(HttpHeaders headers, WrapperForLDPNR wrapper) {
		headers.add(com.base22.carbon.constants.HttpHeaders.LOCATION, wrapper.getURI());
	}

	private void addContentTypeHeader(HttpHeaders headers, WrapperForLDPNR wrapper) {
		headers.add(com.base22.carbon.constants.HttpHeaders.CONTENT_TYPE, wrapper.getContentType());
	}

	private void addLinkTypeHeaders(HttpHeaders headers, WrapperForLDPNR wrapper) {
		HttpHeaderValue header = null;

		header = new HttpHeaderValue();
		header.setMainValue(LDPR.Resources.CLASS.getUri());
		header.setExtendingKey("rel");
		header.setExtendingValue("type");

		headers.add(com.base22.carbon.constants.HttpHeaders.LINK, header.toString());

		header.setMainValue(LDPNR.NR_TYPE);

		headers.add(com.base22.carbon.constants.HttpHeaders.LINK, header.toString());
	}

	private void addETagHeader(HttpHeaders headers, WrapperForLDPNR wrapper) {
		if ( wrapper.getETag() != null ) {
			headers.add(com.base22.carbon.constants.HttpHeaders.ETAG, HttpUtil.formatWeakETag(wrapper.getETag()));
		}
	}

	private void addDescribedByHeader(HttpHeaders headers, WrapperForLDPNR wrapper) {
		StringBuilder describedByBuilder = new StringBuilder();
		describedByBuilder.append("<").append(wrapper.getURI()).append(">");

		HttpHeaderValue headerValue = new HttpHeaderValue();
		headerValue.setMainValue(describedByBuilder.toString());
		headerValue.setExtendingKey("rel");
		headerValue.setExtendingValue("describedby");

		headers.add(com.base22.carbon.constants.HttpHeaders.LINK, headerValue.toString());
	}
}
