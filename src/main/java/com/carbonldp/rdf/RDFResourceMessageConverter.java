package com.carbonldp.rdf;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.http.Link;
import com.carbonldp.web.converters.ModelMessageConverter;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpHeaders;

public class RDFResourceMessageConverter extends ModelMessageConverter<RDFResource> {

	public RDFResourceMessageConverter() {
		super( false, true );
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return RDFResource.class.isAssignableFrom( clazz );
	}

	@Override
	protected Model getModelToWrite( RDFResource model ) {
		return model.getDocument();
	}

	@Override
	protected void setAdditionalHeaders( RDFResource model, HttpHeaders headers ) {
		addRDFResourceLinkHeader( headers );
	}

	private void addRDFResourceLinkHeader( HttpHeaders headers ) {
		Link link = new Link( RDFResourceDescription.Resource.CLASS.getIRI().stringValue() );
		link.addRelationshipType( Consts.TYPE );
		headers.add( HTTPHeaders.LINK, link.toString() );
	}
}