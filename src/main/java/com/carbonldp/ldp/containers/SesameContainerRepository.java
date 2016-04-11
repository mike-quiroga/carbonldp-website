package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.nonrdf.RDFRepresentationFactory;
import com.carbonldp.ldp.nonrdf.RDFRepresentationRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;

import static com.carbonldp.Consts.*;

@Transactional
public class SesameContainerRepository extends AbstractSesameLDPRepository implements ContainerRepository {

	private final RDFSourceRepository sourceRepository;
	private final List<TypedContainerRepository> typedContainerRepositories;
	private RDFRepresentationRepository rdfRepresentationRepository;

	public SesameContainerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository, List<TypedContainerRepository> typedContainerRepositories,
		RDFRepresentationRepository rdfRepresentationRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );

		Assert.notNull( sourceRepository );
		this.sourceRepository = sourceRepository;

		Assert.notNull( typedContainerRepositories );
		Assert.notEmpty( typedContainerRepositories );
		this.typedContainerRepositories = typedContainerRepositories;

		Assert.notNull( rdfRepresentationRepository );
		this.rdfRepresentationRepository = rdfRepresentationRepository;
	}

	@Override
	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return hasMember( containerIRI, possibleMemberIRI, containerType );
	}

	@Override
	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI, Type containerType ) {
		return getTypedRepository( containerType ).hasMember( containerIRI, possibleMemberIRI );
	}

	@Override
	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return hasMembers( containerIRI, sparqlSelector, bindings, containerType );
	}

	@Override
	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, Type containerType ) {
		return getTypedRepository( containerType ).hasMembers( containerIRI, sparqlSelector, bindings );
	}

	@Override
	public Type getContainerType( IRI containerIRI ) {
		Set<IRI> resourceTypes = resourceRepository.getTypes( containerIRI );
		return ContainerFactory.getInstance().getContainerType( resourceTypes );
	}

	@Override
	public Set<Statement> getProperties( IRI containerIRI ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return getProperties( containerIRI, containerType );
	}

	@Override
	public Set<Statement> getProperties( IRI containerIRI, Type containerType ) {
		return getTypedRepository( containerType ).getProperties( containerIRI );
	}

	@Override
	public Set<IRI> getContainedIRIs( IRI containerIRI ) {
		return resourceRepository.getIRIs( containerIRI, ContainerDescription.Property.CONTAINS );
	}

	private static final String getContainmentTriples_query;

	static {
		getContainmentTriples_query = "" +
			"CONSTRUCT {" + NEW_LINE +
			TAB + "?containerIRI ?p ?o" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?containerIRI {" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?containerIRI", "?p", "?o", ContainerDescription.Property.CONTAINS ) + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<Statement> getContainmentTriples( IRI containerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "containerIRI", containerIRI );
		return sparqlTemplate.executeGraphQuery( getContainmentTriples_query, bindings, queryResult -> {
			Set<Statement> statements = new HashSet<>();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( statements );
			handler.handle( queryResult );

			return statements;
		} );
	}

	@Override
	public Set<Statement> getMembershipTriples( IRI containerIRI ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );
		return getMembershipTriples( containerIRI, containerType );
	}

	@Override
	public Set<Statement> getMembershipTriples( IRI containerIRI, Type containerType ) {
		return getTypedRepository( containerType ).getMembershipTriples( containerIRI );
	}

	@Override
	public Set<ContainerRetrievalPreference> getRetrievalPreferences( IRI containerIRI ) {
		Set<IRI> preferenceIRIs = resourceRepository.getIRIs( containerIRI, ContainerDescription.Property.DEFAULT_RETRIEVE_PREFERENCE );
		return RDFNodeUtil.findByIRIs( preferenceIRIs, ContainerRetrievalPreference.class );
	}

	@Override
	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return findMembers( containerIRI, sparqlSelector, bindings, containerType );
	}

	@Override
	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, Type containerType ) {
		return getTypedRepository( containerType ).findMembers( containerIRI, sparqlSelector, bindings );
	}

	@Override
	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		return filterMembers( containerIRI, possibleMemberIRIs, containerType );
	}

	@Override
	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs, Type containerType ) {
		return getTypedRepository( containerType ).filterMembers( containerIRI, possibleMemberIRIs );
	}

	@Override
	public void createChild( IRI containerIRI, RDFSource child ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		createChild( containerIRI, child, containerType );
	}

	@Override
	public void createChild( IRI containerIRI, RDFSource child, Type containerType ) {
		documentRepository.addDocument( child.getDocument() );
		addContainedResource( containerIRI, child.getIRI() );
		getTypedRepository( containerType ).addMember( containerIRI, child.getIRI() );

	}

	@Override
	public void create( Container container ) {
		documentRepository.addDocument( container.getDocument() );
	}

	@Override
	public void createNonRDFResource( IRI containerIRI, IRI resourceIRI, File requestEntity, String mediaType ) {
		DateTime creationTime = DateTime.now();

		RDFRepresentation rdfRepresentation = RDFRepresentationFactory.getInstance().create( resourceIRI );

		rdfRepresentation.add( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL.getIRI(), RDFSourceDescription.Resource.CLASS.getIRI() );
		rdfRepresentation.setTimestamps( creationTime );
		rdfRepresentationRepository.create( rdfRepresentation, requestEntity, mediaType );

		addContainedResource( containerIRI, rdfRepresentation.getIRI() );

		addMember( containerIRI, resourceIRI );
		sourceRepository.touch( containerIRI, creationTime );
	}

	@Override
	public void addMember( IRI containerIRI, IRI member ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		addMember( containerIRI, member, containerType );
	}

	@Override
	public void addMember( IRI containerIRI, IRI member, Type containerType ) {
		getTypedRepository( containerType ).addMember( containerIRI, member );
	}

	@Override
	public void removeMember( IRI containerIRI, IRI member ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		removeMember( containerIRI, member, containerType );
	}

	@Override
	public void removeMember( IRI containerIRI, IRI member, Type containerType ) {
		getTypedRepository( containerType ).removeMember( containerIRI, member );
	}

	@Override
	public void removeMembers( IRI containerIRI ) {
		Type containerType = getContainerType( containerIRI );
		if ( containerType == null ) throw new IllegalStateException( "The resource isn't a container." );

		removeMembers( containerIRI, containerType );
	}

	@Override
	public void removeMembers( IRI containerIRI, Type containerType ) {
		getTypedRepository( containerType ).removeMembers( containerIRI );
	}

	private void addContainedResource( IRI containerIRI, IRI resourceIRI ) {
		connectionTemplate.write( ( connection ) -> connection.add( containerIRI, ContainerDescription.Property.CONTAINS.getIRI(), resourceIRI, containerIRI ) );
	}

	@Override
	public TypedContainerRepository getTypedRepository( Type containerType ) {
		for ( TypedContainerRepository service : typedContainerRepositories ) {
			if ( service.supports( containerType ) ) return service;
		}
		throw new IllegalArgumentException( "The containerType provided isn't supported" );
	}

}
