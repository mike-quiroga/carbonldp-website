package com.carbonldp.ldp.web;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.Vars;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.http.Link;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.containers.ContainerFactory;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.rdf.URIObject;
import com.carbonldp.sparql.SPARQLService;
import com.carbonldp.utils.*;
import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.PreconditionFailedException;
import com.carbonldp.web.exceptions.PreconditionRequiredException;
import org.joda.time.DateTime;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractLDPRequestHandler extends AbstractRequestHandler {
	public static final HTTPHeaderValue interactionModelApplied;

	static {
		interactionModelApplied = new HTTPHeaderValue();
		interactionModelApplied.setMainKey( "rel" );
		interactionModelApplied.setMainValue( "interaction-model" );
	}

	protected ConfigurationRepository configurationRepository;

	protected RDFSourceService sourceService;
	protected ContainerService containerService;
	protected NonRDFSourceService nonRdfSourceService;
	protected SPARQLService sparqlService;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected List<HTTPHeaderValue> appliedPreferences;

	private Set<InteractionModel> supportedInteractionModels;
	private InteractionModel defaultInteractionModel;

	protected void setUp( HttpServletRequest request, HttpServletResponse response ) {
		this.request = request;
		this.response = response;
		this.appliedPreferences = new ArrayList<HTTPHeaderValue>();
	}

	protected boolean targetResourceExists( URI targetURI ) {
		return sourceService.exists( targetURI );
	}

	protected String getTargetURL( HttpServletRequest request ) {
		String requestURI = request.getRequestURI();
		String platformDomain = Vars.getHost();
		StringBuilder targetURIBuilder = new StringBuilder();
		targetURIBuilder.append( platformDomain.substring( 0, platformDomain.length() - 1 ) ).append( requestURI );
		return targetURIBuilder.toString();
	}

	protected URI getTargetURI( HttpServletRequest request ) {
		String url = getTargetURL( request );
		return new URIImpl( url );
	}

	protected InteractionModel getInteractionModel( URI targetURI ) {
		InteractionModel requestInteractionModel = getRequestInteractionModel( request );
		if ( requestInteractionModel != null ) {
			checkInteractionModelSupport( requestInteractionModel );
			appliedPreferences.add( interactionModelApplied );
			return requestInteractionModel;
		}
		return getDefaultInteractionModel( targetURI );
	}

	private InteractionModel getRequestInteractionModel( HttpServletRequest request ) {
		HTTPHeader preferHeader = new HTTPHeader( request.getHeaders( HTTPHeaders.PREFER ) );

		// TODO: Move this to a constants file
		List<HTTPHeaderValue> filteredValues = HTTPHeader.filterHeaderValues( preferHeader, null, null, "rel", "interaction-model" );
		int size = filteredValues.size();
		if ( size == 0 ) return null;
		if ( size > 1 ) throw new BadRequestException( 0x5002 );

		String interactionModelURI = filteredValues.get( 0 ).getMainValue();
		InteractionModel interactionModel = RDFNodeUtil.findByURI( interactionModelURI, InteractionModel.class );
		if ( interactionModel == null ) throw new BadRequestException( 0x5003 );
		return interactionModel;
	}

	private void checkInteractionModelSupport( InteractionModel requestInteractionModel ) {
		if ( ! getSupportedInteractionModels().contains( requestInteractionModel ) ) {
			throw new BadRequestException( 0x5004 );
		}
	}

	private InteractionModel getDefaultInteractionModel( URI targetURI ) {
		URI dimURI = sourceService.getDefaultInteractionModel( targetURI );
		if ( dimURI == null ) return getDefaultInteractionModel();

		InteractionModel sourceDIM = RDFNodeUtil.findByURI( dimURI, InteractionModel.class );
		if ( sourceDIM == null ) return getDefaultInteractionModel();

		if ( ! getSupportedInteractionModels().contains( sourceDIM ) ) return getDefaultInteractionModel();
		return sourceDIM;
	}

	protected void setAppliedPreferenceHeaders() {
		for ( HTTPHeaderValue appliedPreference : appliedPreferences ) {
			response.addHeader( HTTPHeaders.PREFERENCE_APPLIED, appliedPreference.toString() );
		}
	}

	protected void setETagHeader( DateTime modifiedTime ) {
		response.setHeader( HTTPHeaders.ETAG, HTTPUtil.formatWeakETag( modifiedTime.toString() ) );
	}

	protected void setLocationHeader( URIObject uriObject ) {
		response.setHeader( HTTPHeaders.LOCATION, uriObject.getURI().stringValue() );
	}

	protected void addTypeLinkHeader( RDFNodeEnum interactionModel ) {
		Link link = new Link( interactionModel.getURI().stringValue() );
		link.addRelationshipType( Consts.TYPE );

		response.addHeader( HTTPHeaders.LINK, link.toString() );
	}

	protected void addContainerTypeLinkHeader( Container container ) {
		ContainerDescription.Type containerType = ContainerFactory.getInstance().getContainerType( container );
		if ( containerType == null ) containerType = containerService.getContainerType( container.getURI() );

		addTypeLinkHeader( ContainerDescription.Resource.CLASS );
		addTypeLinkHeader( containerType );
	}

	protected String getRequestETag() {
		return request.getHeader( HTTPHeaders.IF_MATCH );
	}

	protected void checkPrecondition( URI targetURI, String requestETag ) {
		if ( requestETag == null ) throw new PreconditionRequiredException();

		DateTime eTagDateTime;
		try {
			eTagDateTime = HTTPUtil.getETagDateTime( requestETag );
		} catch ( IllegalArgumentException e ) {
			throw new PreconditionFailedException( 0x5005 );
		}

		DateTime modified = sourceService.getModified( targetURI );

		if ( ! modified.equals( eTagDateTime ) ) throw new PreconditionFailedException( 0x5006 );
	}

	protected void seekForOrphanFragments( AbstractModel requestModel, RDFResource requestDocumentResource ) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( ! ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( ! URIUtil.hasFragment( subjectURI ) ) continue;
			URI documentURI = new URIImpl( URIUtil.getDocumentURI( subjectURI.stringValue() ) );
			if ( ! requestDocumentResource.getURI().equals( documentURI ) ) {
				throw new BadRequestException( "The request contains orphan fragments." );
			}
		}
	}

	protected Set<InteractionModel> getSupportedInteractionModels() {
		return supportedInteractionModels;
	}

	protected void setSupportedInteractionModels( Set<InteractionModel> supportedInteractionModels ) {
		this.supportedInteractionModels = supportedInteractionModels;
	}

	protected InteractionModel getDefaultInteractionModel() {
		return defaultInteractionModel;
	}

	protected void setDefaultInteractionModel( InteractionModel defaultInteractionModel ) {
		this.defaultInteractionModel = defaultInteractionModel;
	}

	@Autowired
	public void setConfigurationRepository( ConfigurationRepository configurationRepository ) {
		this.configurationRepository = configurationRepository;
	}

	@Autowired
	public void setRDFSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) {
		this.containerService = containerService;
	}

	@Autowired
	public void setNonRDFResourceService( NonRDFSourceService nonRdfSourceService ) {
		this.nonRdfSourceService = nonRdfSourceService;
	}

	@Autowired
	public void setSparqlService( SPARQLService sparqlService ) {
		this.sparqlService = sparqlService;
	}

}
