package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.http.OrderBy;
import com.carbonldp.http.OrderByRetrievalPreferences;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.nonrdf.RDFRepresentationFactory;
import com.carbonldp.ldp.nonrdf.RDFRepresentationRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.namespaces.XSD;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;

import static com.carbonldp.Consts.*;
import static com.carbonldp.namespaces.XSD.Properties.STRING;

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

	@Override
	public Set<IRI> getContainedIRIs( IRI targetIRI, OrderByRetrievalPreferences orderByRetrievalPreferences ) {
		String queryString = createGetSubjectsWithPreferencesQuery( targetIRI, ContainerDescription.Property.CONTAINS, orderByRetrievalPreferences );
		return executeGetSubjectsWithPreferencesQuery( queryString );
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

	@Override
	public Set<IRI> getMemberIRIs( IRI targetIRI, OrderByRetrievalPreferences orderByRetrievalPreferences ) {
		TypedContainerRepository repositoryType = getTypedRepository( getContainerType( targetIRI ) );
		IRI membershipResource = repositoryType.getMembershipResource( targetIRI );
		IRI hasMemberRelation = repositoryType.getHasMemberRelation( targetIRI );

		String queryString = createGetSubjectsWithPreferencesQuery( membershipResource, hasMemberRelation, orderByRetrievalPreferences );
		return executeGetSubjectsWithPreferencesQuery( queryString );

	}

	@Override
	public Set<IRI> getMemberIRIs( IRI targetIRI ) {
		TypedContainerRepository repositoryType = getTypedRepository( getContainerType( targetIRI ) );
		IRI membershipResource = repositoryType.getMembershipResource( targetIRI );
		IRI hasMemberRelation = repositoryType.getHasMemberRelation( targetIRI );

		return resourceRepository.getIRIs( membershipResource, hasMemberRelation );
	}

	public String createGetSubjectsWithPreferencesQuery( IRI targetIRI, Collection<IRI> predicateEnum, OrderByRetrievalPreferences orderByRetrievalPreferences ) {
		StringBuilder query = new StringBuilder();
		StringBuilder filter = new StringBuilder();
		StringBuilder orderByString = new StringBuilder();
		StringBuilder numericValues = new StringBuilder();
		query.append( NEW_LINE )
		     .append( "SELECT DISTINCT ?subject" ).append( NEW_LINE )
		     .append( "WHERE {" ).append( NEW_LINE )
		     .append( TAB ).append( SPARQLUtil.assignVar( "predicate", predicateEnum ) ).append( NEW_LINE )
		     .append( TAB ).append( "<" ).append( targetIRI.stringValue() ).append( "> ?predicate ?subject ." ).append( NEW_LINE )
		     .append( TAB ).append( SPARQLUtil.assignVar( "a", RDFSourceDescription.Property.TYPE ) ).append( NEW_LINE )
		     .append( TAB ).append( "?subject ?a ?object ." ).append( NEW_LINE );
		filter.append( TAB ).append( "FILTER ( " ).append( NEW_LINE );
		orderByString.append( "ORDER BY" );
		boolean hasFilter = false;
		List<OrderBy> orderByList = orderByRetrievalPreferences.getOrderByList();
		if ( orderByList != null && ( ! orderByList.isEmpty() ) ) {
			int i = 1;
			for ( OrderBy orderBy : orderByList ) {
				String property = orderBy.getProperty();
				query.append( TAB ).append( "?subject " ).append( property ).append( " ?value" ).append( i ).append( "." ).append( NEW_LINE );
				String literalType = orderBy.getLiteralType();
				String lang = orderBy.getLanguage();
				if ( literalType != null ) {
					if ( hasFilter ) filter.append( "&&" ).append( NEW_LINE );

					if ( literalType.equals( "numeric" ) ) {
						if ( numericValues.length() == 0 ) numericValues.append( SPARQLUtil.assignVar( "numeric", getNumericValues() ) ).append( NEW_LINE );
						filter.append( TAB ).append( TAB ).append( "(datatype(?value" ).append( i ).append( ") = ?numeric)" ).append( NEW_LINE );
						hasFilter = true;
					} else {
						if ( ! literalType.equals( "<" + STRING + ">" ) || lang == null ) {
							filter.append( TAB ).append( TAB ).append( "(datatype(?value" ).append( i ).append( ") = " ).append( literalType ).append( ")" ).append( NEW_LINE );
							hasFilter = true;
						}
					}
				}
				if ( lang != null ) {
					if ( hasFilter ) filter.append( "&&" ).append( NEW_LINE );
					filter.append( TAB ).append( TAB ).append( "(langMatches( lang( ?value" ).append( i ).append( ") , \"" ).append( lang ).append( "\") )" ).append( NEW_LINE );
					hasFilter = true;
				}
				if ( ! orderBy.isAscending() ) {
					orderByString.append( " DESC( ?value" ).append( i ).append( ")" );
				} else {
					orderByString.append( " ?value" ).append( i );
				}
				i++;
			}
		} else {
			orderByString.append( " ?subject" );
		}
		if ( hasFilter ) {
			filter.append( TAB ).append( ")" ).append( NEW_LINE );
			query.append( numericValues );
			query.append( filter );
		}
		query.append( "}" ).append( NEW_LINE );
		query.append( orderByString ).append( NEW_LINE );
		String limitString = orderByRetrievalPreferences.getLimit();
		if ( limitString != null )
			query.append( "LIMIT " ).append( limitString ).append( NEW_LINE );

		String offsetString = orderByRetrievalPreferences.getOffset();
		if ( offsetString != null )
			query.append( "OFFSET " ).append( offsetString ).append( NEW_LINE );

		return query.toString();
	}

	private String createGetSubjectsWithPreferencesQuery( IRI targetIRI, RDFNodeEnum predicateEnum, OrderByRetrievalPreferences orderByRetrievalPreferences ) {
		return createGetSubjectsWithPreferencesQuery( targetIRI, Arrays.asList( predicateEnum.getIRIs() ), orderByRetrievalPreferences );
	}

	private String createGetSubjectsWithPreferencesQuery( IRI targetIRI, IRI predicateIRI, OrderByRetrievalPreferences orderByRetrievalPreferences ) {
		return createGetSubjectsWithPreferencesQuery( targetIRI, Arrays.asList( predicateIRI ), orderByRetrievalPreferences );
	}

	private Set<IRI> getNumericValues() {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		Set<IRI> numericValues = new HashSet<>();

		numericValues.add( valueFactory.createIRI( XSD.Properties.BYTE ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.DECIMAL ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.INT ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.INTEGER ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.LONG ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.NEGATIVEINTEGER ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.NONNEGATIVEINTEGER ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.NONPOSITIVEINTEGER ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.POSITIVEINTEGER ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.SHORT ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.UNSIGNEDLONG ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.UNSIGNEDINT ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.UNSIGNEDSHORT ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.UNSIGNEDBYTE ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.DOUBLE ) );
		numericValues.add( valueFactory.createIRI( XSD.Properties.FLOAT ) );

		return numericValues;
	}

	private Set<IRI> executeGetSubjectsWithPreferencesQuery( String queryString ) {
		return sparqlTemplate.executeTupleQuery( queryString, result -> {
			Set<IRI> childrenIRIs = new HashSet<>();
			while ( result.hasNext() ) {
				Value childValue = result.next().getBinding( "subject" ).getValue();
				if ( ! ValueUtil.isIRI( childValue ) ) throw new IllegalStateException( "child is not a IRI" );
				childrenIRIs.add( ValueUtil.getIRI( childValue ) );
			}
			return childrenIRIs;
		} );
	}

}
