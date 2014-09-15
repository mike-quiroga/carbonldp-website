package com.base22.carbon.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.ldp.models.RDFResourceFactory;
import com.base22.carbon.models.APIDescriptionClass.Properties;
import com.base22.carbon.models.APIDescriptionClass.Resources;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PlatformAPIGetRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		RDFResource apiDescription = getAPIDescription();

		return new ResponseEntity<Object>(apiDescription, HttpStatus.OK);
	}

	private RDFResource getAPIDescription() {
		return constructAPIDescription();
	}

	// TODO: Move this
	private RDFResource constructAPIDescription() {
		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(Carbon.URL)
			.append("/api")
		;
		//@formatter:on

		RDFResourceFactory factory = new RDFResourceFactory();
		RDFResource apiDescription = factory.create(uriBuilder.toString());

		apiDescription.addType(Resources.CLASS.getResource());
		apiDescription.addProperty(Properties.VERSION.getProperty(), configurationService.getPlatformVersion());

		return apiDescription;
	}
}
