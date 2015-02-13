package com.carbonldp.ldp.web.handlers;

import static com.carbonldp.commons.Consts.SLASH;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;

import com.carbonldp.commons.HTTPHeaders;
import com.carbonldp.commons.models.RDFResource;
import com.carbonldp.commons.utils.HTTPUtil;
import com.carbonldp.web.AbstractRequestHandler;

public abstract class AbstractPOSTRequestHandler extends AbstractRequestHandler {
	protected URI forgeRequestResourceURI(RDFResource requestResource, HttpServletRequest request, HttpServletResponse response) {

		// TODO: FT
		return null;
	}

	protected String forgeDocumentResourceURI(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(parentURI);

		if ( ! parentURI.endsWith(SLASH) ) uriBuilder.append(SLASH);

		uriBuilder.append(forgeSlug(documentResource, parentURI, request));

		return uriBuilder.toString();
	}

	private String forgeSlug(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		String uriSlug = configurationRepository.getGenericRequestSlug(documentResource.getURI().stringValue());
		String slug = uriSlug != null ? uriSlug : request.getHeader(HTTPHeaders.SLUG);

		if ( slug != null ) {
			if ( slug.endsWith(SLASH) ) {
				slug = slug.substring(0, slug.length() - 1);
				slug = HTTPUtil.createSlug(slug).concat(SLASH);
			} else slug = HTTPUtil.createSlug(slug);
			return slug;
		}

		Random random = new Random();
		return String.valueOf(Math.abs(random.nextLong()));
	}

}