package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.rdf.RDFResourceDescription;
import com.carbonldp.utils.HTTPHeaderUtil;
import com.carbonldp.web.ModelMessageConverter;
import org.openrdf.model.Model;
import org.springframework.http.HttpHeaders;

public class RDFSourceMessageConverter extends ModelMessageConverter<RDFSource> {

	public RDFSourceMessageConverter() {
		super( false, true );
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return RDFSource.class.isAssignableFrom( clazz );
	}

	@Override
	protected Model getModelToWrite( RDFSource model ) {
		return model.getDocument();
	}

	@Override
	protected void setAdditionalHeaders( RDFSource model, HttpHeaders headers ) {
		addRDFResourceLinkHeader( headers );
		setETagHeader( model, headers );
	}

	private void addRDFResourceLinkHeader( HttpHeaders headers ) {
		HTTPHeaderValue typeHeader = HTTPHeaderUtil.createLinkTypeHeader( RDFResourceDescription.Resource.CLASS.getURI() );
		headers.add( HTTPHeaders.LINK, typeHeader.toString() );
	}

	private void setETagHeader( RDFSource model, HttpHeaders headers ) {
		headers.set( HTTPHeaders.ETAG, model.getETag() );
	}
}
