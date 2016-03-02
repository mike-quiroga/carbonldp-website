package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidRDFTypeException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocumentFactory;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.ServicesInvoker;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Set;

public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {

	private ServicesInvoker servicesInvoker;
	private RDFSourceService sourceService;

	@Override
	public Container get( URI containerURI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences ) {
		ContainerDescription.Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new InvalidRDFTypeException( ContainerDescription.Resource.CLASS.getURI().stringValue() );

		Container container = ContainerFactory.getInstance().get( containerURI, containerType );
		for ( APIPreferences.ContainerRetrievalPreference preference : containerRetrievalPreferences ) {
			switch ( preference ) {
				case CONTAINER_PROPERTIES:
					container.getBaseModel().addAll( containerRepository.getProperties( containerURI ) );
					break;
				case CONTAINMENT_TRIPLES:
					container.getBaseModel().addAll( containerRepository.getContainmentTriples( containerURI ) );
					break;
				case CONTAINED_RESOURCES:
					throw new NotImplementedException();
				case MEMBERSHIP_TRIPLES:
					if ( containerRetrievalPreferences.contains( APIPreferences.ContainerRetrievalPreference.NON_READABLE_MEMBERSHIP_RESOURCE_TRIPLES ) ) {
						container.getBaseModel().addAll( getMembershipTriples( containerURI ) );
					} else {
						Set<Statement> membershipTriples = servicesInvoker.proxy( ( proxy ) -> {
							return proxy.getContainerService().getReadableMembershipResourcesTriples( containerURI );
						} );
						container.getBaseModel().addAll( membershipTriples );
					}
					break;
				case MEMBER_RESOURCES:
					throw new NotImplementedException();
				case NON_READABLE_MEMBERSHIP_RESOURCE_TRIPLES:
					if ( ! containerRetrievalPreferences.contains( APIPreferences.ContainerRetrievalPreference.MEMBERSHIP_TRIPLES ) ) {
						Set<Statement> membershipTriples = servicesInvoker.proxy( ( proxy ) -> {
							return proxy.getContainerService().getNonReadableMembershipResourcesTriples( containerURI );
						} );
						container.getBaseModel().addAll( membershipTriples );
					}
					break;
				default:
					throw new IllegalStateException();

			}
		}
		return container;
	}

	public Set<Statement> getMembershipTriples( URI containerURI ) {
		return containerRepository.getMembershipTriples( containerURI );
	}

	public Set<Statement> getReadableMembershipResourcesTriples( URI containerURI ) {
		return containerRepository.getMembershipTriples( containerURI );
	}

	public Set<Statement> getNonReadableMembershipResourcesTriples( URI containerURI ) {
		return containerRepository.getMembershipTriples( containerURI );
	}

	@Override
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( URI containerURI ) {
		return containerRepository.getRetrievalPreferences( containerURI );
	}

	@Override
	public ContainerDescription.Type getContainerType( URI containerURI ) {
		return containerRepository.getContainerType( containerURI );
	}

	@Override
	public DateTime createChild( URI containerURI, BasicContainer basicContainer ) {
		DateTime creationTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerURI ) ).getMembershipResource( containerURI );
		basicContainer.setTimestamps( creationTime );
		validateBasicContainer( basicContainer );

		containerRepository.createChild( containerURI, basicContainer );
		aclRepository.createACL( basicContainer.getURI() );

		sourceRepository.touch( containerURI, creationTime );

		if ( ! membershipResource.equals( containerURI ) ) {
			sourceRepository.touch( membershipResource, creationTime );
		}

		return creationTime;
	}

	protected void validateBasicContainer( RDFResource toValidate ) {
		List<Infraction> infractions = BasicContainerFactory.getInstance().validate( toValidate );
		infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( toValidate.getDocument() ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public void createNonRDFResource( URI targetURI, URI resourceURI, File resourceFile, String mediaType ) {
		if ( ! sourceRepository.exists( targetURI ) ) throw new ResourceDoesntExistException();
		if ( sourceRepository.exists( resourceURI ) ) throw new ResourceAlreadyExistsException();

		containerRepository.createNonRDFResource( targetURI, resourceURI, resourceFile, mediaType );
		aclRepository.createACL( resourceURI );
	}

	@Override
	public void addMembers( URI containerURI, Set<URI> members ) {

		for ( URI member : members ) {
			addMember( containerURI, member );
		}
	}

	@Override
	public void addMember( URI containerURI, URI member ) {
		DateTime modifiedTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerURI ) ).getMembershipResource( containerURI );

		if ( ! sourceRepository.exists( containerURI ) ) throw new ResourceDoesntExistException();
		containerRepository.addMember( containerURI, member );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	@Override
	public void removeMembers( URI containerURI, Set<URI> members ) {
		DateTime modifiedTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerURI ) ).getMembershipResource( containerURI );
		for ( URI member : members ) {
			removeMember( containerURI, member );
		}
		sourceRepository.touch( membershipResource, modifiedTime );

	}

	@Override
	public void removeMember( URI containerURI, URI member ) {
		containerRepository.removeMember( containerURI, member );
	}

	@Override
	public void removeMembers( URI containerURI ) {
		// TODO: Should the resource be touched here?
		containerRepository.removeMembers( containerURI );
	}

	@Override
	public void deleteContainedResources( URI targetURI ) {
		Set<URI> containedURIs = containerRepository.getContainedURIs( targetURI );
		for ( URI containedURI : containedURIs ) {
			sourceService.delete( containedURI );
			sourceRepository.deleteOccurrences( containedURI, true );
		}
	}

	@Override
	public void delete( URI targetURI ) {
		sourceRepository.delete( targetURI );
	}

	@Autowired
	public void setRDFSourceService( RDFSourceService rdfSourceService ) { this.sourceService = rdfSourceService; }

	@Autowired
	public void setServicesInvoker( ServicesInvoker servicesInvoker ) { this.servicesInvoker = servicesInvoker; }
}
