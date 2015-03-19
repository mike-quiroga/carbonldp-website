package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

@Transactional
public class SesameContainerRepository extends AbstractSesameLDPRepository implements ContainerRepository {

	private final List<TypedContainerRepository> typedContainerRepositories;

	public SesameContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository, List<TypedContainerRepository> typedContainerRepositories ) {
		super( connectionFactory, resourceRepository, documentRepository );
		this.typedContainerRepositories = typedContainerRepositories;
	}

	@Override
	public boolean isMember( URI containerURI, URI possibleMemberURI ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return isMember( containerURI, possibleMemberURI, containerType );
	}

	@Override
	public boolean isMember( URI containerURI, URI possibleMemberURI, Type containerType ) {
		return getService( containerType ).isMember( containerURI, possibleMemberURI );
	}

	@Override
	public Container get( URI containerURI, Set<ContainerRetrievalPreference> preferences ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		Container container = ContainerFactory.get( containerURI, containerType );
		for ( ContainerRetrievalPreference preference : preferences ) {
			switch ( preference ) {
				case CONTAINER_PROPERTIES:
					container.getBaseModel().addAll( getProperties( containerURI ) );
					break;
				case CONTAINMENT_TRIPLES:
					container.getBaseModel().addAll( getContainmentTriples( containerURI ) );
					break;
				case CONTAINED_RESOURCES:
					throw new NotImplementedException();
				case MEMBERSHIP_TRIPLES:
					container.getBaseModel().addAll( getMembershipTriples( containerURI ) );
					break;
				case MEMBER_RESOURCES:
					throw new NotImplementedException();
				default:
					throw new IllegalStateException();

			}
		}
		return container;
	}

	@Override
	public Type getContainerType( URI containerURI ) {
		Set<URI> resourceTypes = resourceRepository.getTypes( containerURI );
		for ( URI resourceType : resourceTypes ) {
			Type containerType = RDFNodeUtil.findByURI( resourceType, ContainerDescription.Type.class );
			if ( containerType != null ) return containerType;
		}
		return null;
	}

	@Override
	public Set<Statement> getProperties( URI containerURI ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return getProperties( containerURI, containerType );
	}

	@Override
	public Set<Statement> getProperties( URI containerURI, Type containerType ) {
		return getService( containerType ).getProperties( containerURI );
	}

	private static final String getContainmentTriples_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( "CONSTRUCT {" ).append( NEW_LINE )
			.append( TAB ).append( "?containerURI ?p ?o" ).append( NEW_LINE )
			.append( "} WHERE {" ).append( NEW_LINE )
			.append( TAB ).append( "GRAPH ?containerURI {" ).append( NEW_LINE )
			.append( TAB ).append( TAB ).append( RDFNodeUtil.generatePredicateStatement( "?containerURI", "?p", "?o", ContainerDescription.Property.CONTAINS ) ).append( NEW_LINE )
			.append( TAB ).append( "}" ).append( NEW_LINE )
			.append( "}" )
		;
		getContainmentTriples_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getContainmentTriples( URI containerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerURI", containerURI );
		return sparqlTemplate.executeGraphQuery( getContainmentTriples_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );

			return statements;
		} );
	}

	@Override
	public Set<Statement> getMembershipTriples( URI containerURI ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return getMembershipTriples( containerURI, containerType );
	}

	@Override
	public Set<Statement> getMembershipTriples( URI containerURI, Type containerType ) {
		return getService( containerType ).getMembershipTriples( containerURI );
	}

	@Override
	public Set<ContainerRetrievalPreference> getRetrievalPreferences( URI containerURI ) {
		Set<URI> preferenceURIs = resourceRepository.getURIs( containerURI, ContainerDescription.Property.DEFAULT_RETRIEVE_PREFERENCE );
		return RDFNodeUtil.findByURIs( preferenceURIs, ContainerRetrievalPreference.class );
	}

	@Override
	public Set<URI> findMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return findMembers( containerURI, sparqlSelector, bindings, containerType );
	}

	@Override
	public Set<URI> findMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings, Type containerType ) {
		return getService( containerType ).findMembers( containerURI, sparqlSelector, bindings );
	}

	@Override
	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return filterMembers( containerURI, possibleMemberURIs, containerType );
	}

	@Override
	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs, Type containerType ) {
		return getService( containerType ).filterMembers( containerURI, possibleMemberURIs );
	}

	@Override
	public void createChild( URI containerURI, RDFSource child ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		createChild( containerURI, child, containerType );
	}

	@Override
	public void createChild( URI containerURI, RDFSource child, Type containerType ) {
		addContainedResource( containerURI, child.getURI() );
		child = getService( containerType ).addMember( containerURI, child );
		documentRepository.addDocument( child.getDocument() );
	}

	@Override
	public void create( Container container ) {
		documentRepository.addDocument( container.getDocument() );
	}

	@Override
	public void addMember( URI containerURI, RDFSource member ) {
		Type containerType = getContainerType( containerURI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		addMember( containerURI, member, containerType );
	}

	@Override
	public void addMember( URI containerURI, RDFSource member, Type containerType ) {
		getService( containerType ).addMember( containerURI, member );
	}

	private void addContainedResource( URI containerURI, URI resourceURI ) {
		connectionTemplate.write( ( connection ) -> connection.add( containerURI, ContainerDescription.Property.CONTAINS.getURI(), resourceURI, containerURI ) );
	}

	private TypedContainerRepository getService( Type containerType ) {
		for ( TypedContainerRepository service : typedContainerRepositories ) {
			if ( service.supports( containerType ) ) return service;
		}
		return null;
	}
}
