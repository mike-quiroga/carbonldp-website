package com.base22.carbon.web;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.base22.carbon.Carbon;
import com.base22.carbon.HttpHeaderValue;
import com.base22.carbon.ldp.LDPResource;
import com.base22.carbon.ldp.web.LDPResourceMessageConverter;
import com.base22.carbon.models.ErrorResponse;

public class ErrorResponseMessageConverter extends LDPResourceMessageConverter {

	// The languages are added in order of preferance. The default language goes first.
	public ErrorResponseMessageConverter() {
		super();
	}

	protected boolean supports(Class<?> clazz) {
		boolean supports = ErrorResponse.class.isAssignableFrom(clazz);
		return supports;
	}

	@Override
	public void write(LDPResource ldpResource, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		ErrorResponse errorResponse = (ErrorResponse) ldpResource;

		HttpHeaders headers = outputMessage.getHeaders();

		addDescribedByHeader(headers, errorResponse);

		super.write(ldpResource, contentType, outputMessage);
	}

	private void addDescribedByHeader(HttpHeaders headers, ErrorResponse errorResponse) {
		String errorCode = null;
		if ( errorResponse.getCarbonCode() != null ) {
			errorCode = errorResponse.getCarbonCode();
		} else {
			errorCode = String.valueOf(errorResponse.getHttpStatus().value());
		}

		StringBuilder linkBuilder = new StringBuilder();

		//@formatter:off
		linkBuilder
			.append("<")
			.append(Carbon.URL)
			.append("/docs/errors/")
			.append(errorCode)
			.append(">")
		;
		//@formatter:on

		HttpHeaderValue headerValue = new HttpHeaderValue();
		headerValue.setMainValue(linkBuilder.toString());
		headerValue.setExtendingKey("rel");
		headerValue.setExtendingValue("describedby");

		headers.add(com.base22.carbon.HttpHeaders.LINK, headerValue.toString());
	}

}
