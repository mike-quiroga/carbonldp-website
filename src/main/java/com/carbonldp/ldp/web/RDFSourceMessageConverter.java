package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.web.ModelMessageConverter;
import org.openrdf.model.Model;
import org.springframework.http.HttpHeaders;

import static com.carbonldp.Consts.*;

public class RDFSourceMessageConverter extends ModelMessageConverter<RDFSource> {

	public RDFSourceMessageConverter() {
		super( false, true );
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return RDFSource.class.isAssignableFrom( clazz );
	}

	@Override
	protected Model getModelToWrite(RDFSource model) {
		return model.getDocument();
	}

	@Override
	protected void setAdditionalHeaders(RDFSource model, HttpHeaders headers) {
		setLinkHeader( headers );
		setETagHeader( model, headers );
	}

	private void setLinkHeader(HttpHeaders headers) {
		headers.set( HTTPHeaders.LINK, getLinkHeader( RDFResourceDescription.Resource.CLASS ) );
		headers.set( HTTPHeaders.LINK, getLinkHeader( RDFSourceDescription.Resource.CLASS ) );
	}

	private String getLinkHeader(RDFNodeEnum classNodeEnum) {
		HTTPHeaderValue headerValue = new HTTPHeaderValue();

		StringBuilder mainBuilder = new StringBuilder();
		mainBuilder.append( LESS_THAN ).append( classNodeEnum.getURI().stringValue() ).append( MORE_THAN );

		headerValue.setMain( mainBuilder.toString() );
		headerValue.setExtendingKey( REL );
		headerValue.setExtendingValue( TYPE );

		return headerValue.toString();
	}

	private void setETagHeader(RDFSource model, HttpHeaders headers) {
		headers.set( HTTPHeaders.ETAG, model.getETag() );
	}
}
