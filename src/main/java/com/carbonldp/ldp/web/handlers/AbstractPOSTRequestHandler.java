package com.carbonldp.ldp.web.handlers;

import static com.carbonldp.Consts.SLASH;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.models.RDFResource;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.web.AbstractRequestHandler;

public abstract class AbstractPOSTRequestHandler extends AbstractRequestHandler {
	protected URI forgeRequestResourceURI(RDFResource requestResource, HttpServletRequest request, HttpServletResponse response) {

		// TODO: FT
		return null;
	}

	protected URI forgeUniqueURI(RDFResource requestResource, String parentURI, HttpServletRequest request) {
		// TODO: Check that the resourceURI is unique and if not forge another one
		return forgeDocumentResourceURI(requestResource, parentURI, request);
	}

	protected URI forgeDocumentResourceURI(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(parentURI);

		if ( ! parentURI.endsWith(SLASH) ) uriBuilder.append(SLASH);

		uriBuilder.append(forgeSlug(documentResource, parentURI, request));

		return new URIImpl(uriBuilder.toString());
	}

	private String forgeSlug(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		String uriSlug = configurationRepository.getGenericRequestSlug(documentResource.getURI().stringValue());
		String slug = uriSlug != null ? uriSlug : request.getHeader(HTTPHeaders.SLUG);

		if ( slug != null ) {
			if ( slug.endsWith(SLASH) ) {
				slug = slug.substring(0, slug.length() - 1);
				slug = HTTPUtil.createSlug(slug).concat(SLASH);
			} else slug = HTTPUtil.createSlug(slug);
		} else {
			Random random = new Random();
			slug = String.valueOf(Math.abs(random.nextLong()));
		}

		if ( configurationRepository.enforceEndingSlash() && ! slug.endsWith(SLASH) ) slug = slug.concat(SLASH);

		return slug;
	}

}