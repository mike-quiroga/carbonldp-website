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
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.spring.ServicesInvoker;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Set;

public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {

	private ServicesInvoker servicesInvoker;
	private RDFSourceService sourceService;
	private RDFResourceRepository resourceRepository;

	@Override
	public Container get( IRI containerIRI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences ) {
		ContainerDescription.Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new InvalidRDFTypeException( ContainerDescription.Resource.CLASS.getIRI().stringValue() );

		Container container = ContainerFactory.getInstance().get( containerIRI, containerType );
		for ( APIPreferences.ContainerRetrievalPreference preference : containerRetrievalPreferences ) {
			switch ( preference ) {
				case CONTAINER_PROPERTIES:
					container.getBaseModel().addAll( containerRepository.getProperties( containerIRI ) );
					break;
				case CONTAINMENT_TRIPLES:
					container.getBaseModel().addAll( containerRepository.getContainmentTriples( containerIRI ) );
					break;
				case CONTAINED_RESOURCES:
					throw new NotImplementedException();
				case MEMBERSHIP_TRIPLES:
					if ( containerRetrievalPreferences.contains( APIPreferences.ContainerRetrievalPreference.NON_READABLE_MEMBERSHIP_RESOURCE_TRIPLES ) ) {
						container.getBaseModel().addAll( getMembershipTriples( containerIRI ) );
					} else {
						Set<Statement> membershipTriples = servicesInvoker.proxy( ( proxy ) -> {
							return proxy.getContainerService().getReadableMembershipResourcesTriples( containerIRI );
						} );
						container.getBaseModel().addAll( membershipTriples );
					}
					break;
				case MEMBER_RESOURCES:
					throw new NotImplementedException();
				case NON_READABLE_MEMBERSHIP_RESOURCE_TRIPLES:
					if ( ! containerRetrievalPreferences.contains( APIPreferences.ContainerRetrievalPreference.MEMBERSHIP_TRIPLES ) ) {
						Set<Statement> membershipTriples = servicesInvoker.proxy( ( proxy ) -> {
							return proxy.getContainerService().getNonReadableMembershipResourcesTriples( containerIRI );
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

	public Set<Statement> getMembershipTriples( IRI containerIRI ) {
		return containerRepository.getMembershipTriples( containerIRI );
	}

	public Set<Statement> getReadableMembershipResourcesTriples( IRI containerIRI ) {
		return containerRepository.getMembershipTriples( containerIRI );
	}

	public Set<Statement> getNonReadableMembershipResourcesTriples( IRI containerIRI ) {
		return containerRepository.getMembershipTriples( containerIRI );
	}

	@Override
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( IRI containerIRI ) {
		return containerRepository.getRetrievalPreferences( containerIRI );
	}

	@Override
	public ContainerDescription.Type getContainerType( IRI containerIRI ) {
		return containerRepository.getContainerType( containerIRI );
	}

	@Override
	public DateTime createChild( IRI containerIRI, BasicContainer basicContainer ) {
		DateTime creationTime = DateTime.now();
		IRI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerIRI ) ).getMembershipResource( containerIRI );
		basicContainer.setTimestamps( creationTime );
		validateBasicContainer( basicContainer );

		containerRepository.createChild( containerIRI, basicContainer );
		aclRepository.createACL( basicContainer.getIRI() );

		sourceRepository.touch( containerIRI, creationTime );

		if ( ! membershipResource.equals( containerIRI ) ) {
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
	public void createNonRDFResource( IRI targetIRI, IRI resourceIRI, File resourceFile, String mediaType ) {
		if ( ! sourceRepository.exists( targetIRI ) ) throw new ResourceDoesntExistException();
		if ( sourceRepository.exists( resourceIRI ) ) throw new ResourceAlreadyExistsException();

		containerRepository.createNonRDFResource( targetIRI, resourceIRI, resourceFile, mediaType );
		aclRepository.createACL( resourceIRI );
	}

	@Override
	public void addMembers( IRI containerIRI, Set<IRI> members ) {

		for ( IRI member : members ) {
			addMember( containerIRI, member );
		}
	}

	@Override
	public void addMember( IRI containerIRI, IRI member ) {
		DateTime modifiedTime = DateTime.now();
		IRI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerIRI ) ).getMembershipResource( containerIRI );

		if ( ! sourceRepository.exists( containerIRI ) ) throw new ResourceDoesntExistException();
		containerRepository.addMember( containerIRI, member );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	@Override
	public void removeMembers( IRI containerIRI, Set<IRI> members ) {
		DateTime modifiedTime = DateTime.now();
		IRI membershipResource = containerRepository.getTypedRepository( this.getContainerType( containerIRI ) ).getMembershipResource( containerIRI );
		for ( IRI member : members ) {
			removeMember( containerIRI, member );
		}
		sourceRepository.touch( membershipResource, modifiedTime );

	}

	@Override
	public void removeMember( IRI containerIRI, IRI member ) {
		deleteInvertedRelation( containerIRI, member );
		containerRepository.removeMember( containerIRI, member );
	}

	@Override
	public void removeMembers( IRI containerIRI ) {
		// TODO: Should the resource be touched here?
		containerRepository.removeMembers( containerIRI );
	}

	@Override
	public void deleteContainedResources( IRI targetIRI ) {
		Set<IRI> containedIRIs = containerRepository.getContainedIRIs( targetIRI );
		for ( IRI containedIRI : containedIRIs ) {
			sourceService.delete( containedIRI );
		}
	}

	@Override
	public void delete( IRI targetIRI ) {
		sourceRepository.delete( targetIRI, true );
	}

	private void deleteInvertedRelation( IRI containerIRI, IRI memberIRI ) {
		IRI isMemberOfRelation = resourceRepository.getIRI( containerIRI, ContainerDescription.Property.MEMBER_OF_RELATION );
		if ( isMemberOfRelation == null ) return;
		resourceRepository.remove( memberIRI, isMemberOfRelation, containerIRI );
	}

	@Autowired
	public void setRDFSourceService( RDFSourceService rdfSourceService ) { this.sourceService = rdfSourceService; }

	@Autowired
	public void setServicesInvoker( ServicesInvoker servicesInvoker ) { this.servicesInvoker = servicesInvoker; }

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) { this.resourceRepository = resourceRepository; }
}
