package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.ETagHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.carbonldp.Consts.*;

@Transactional
public class SesameRDFSourceRepository extends AbstractSesameLDPRepository implements RDFSourceRepository {

	private static String isQuery;

	public SesameRDFSourceRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String exists_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "ASK {" ).append( NEW_LINE )
				.append( TAB ).append( "GRAPH ?sourceURI {" ).append( NEW_LINE )
				.append( TAB ).append( TAB ).append( "?sourceURI ?p ?o" ).append( NEW_LINE )
				.append( TAB ).append( "}" ).append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		exists_query = queryBuilder.toString();
	}

	@Override
	public boolean exists( URI sourceURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		return sparqlTemplate.executeBooleanQuery( exists_query, bindings );
	}

	private static final String get_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( "CONSTRUCT {" ).append( NEW_LINE )
				.append( TAB ).append( "?s ?p ?o" ).append( NEW_LINE )
				.append( "} WHERE {" ).append( NEW_LINE )
				.append( TAB ).append( "GRAPH ?sourceURI {" ).append( NEW_LINE )
				.append( TAB ).append( TAB ).append( "?s ?p ?o" ).append( NEW_LINE )
				.append( TAB ).append( "}" ).append( NEW_LINE )
				.append( "}" )
		;
		//@formatter:on
		get_query = queryBuilder.toString();
	}

	@Override
	public RDFSource get( URI sourceURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		return sparqlTemplate.executeGraphQuery( get_query, bindings, queryResult -> {
			AbstractModel model = new LinkedHashModel();
			GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler( model );
			handler.handle( queryResult );

			return new RDFSource( model, sourceURI );
		} );
	}

	@Override
	public int getETag( URI sourceURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		return sparqlTemplate.executeGraphQuery( get_query, bindings, queryResult -> {
			ETagHandler handler = new ETagHandler();
			handler.handle( queryResult );
			return handler.getETagValue();
		} );
	}

	// TODO: Decide. Should it return empty objects?
	@Override
	public Set<RDFSource> get( Set<URI> sourceURIs ) {
		Resource[] contexts = sourceURIs.toArray( new Resource[sourceURIs.size()] );
		AbstractModel sourcesModel = connectionTemplate.readStatements(
			connection -> connection.getStatements( null, null, null, false, contexts ),
			repositoryResult -> {
				AbstractModel model = new LinkedHashModel();
				while ( repositoryResult.hasNext() ) {
					model.add( repositoryResult.next() );
				}
				return model;
			}
		);

		return sourceURIs
			.stream()
			.map( sourceURI -> new RDFSource( sourcesModel, sourceURI ) )
			.collect( Collectors.toSet() );
	}

	@Override
	public DateTime getModified( URI sourceURI ) {
		return resourceRepository.getDate( sourceURI, RDFSourceDescription.Property.MODIFIED );
	}

	private static final String getDefaultInteractionModelQuery;

	static {
		getDefaultInteractionModelQuery = "" +
			"SELECT ?dim WHERE {" + NEW_LINE +
			TAB + "GRAPH ?sourceURI {" + NEW_LINE +
			TAB + TAB + RDFNodeUtil.generatePredicateStatement( "?sourceURI", "?dim", RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL ) + NEW_LINE +
			TAB + TAB + "FILTER( isURI(?dim) )." + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}" + NEW_LINE +
			"LIMIT 1"
		;
	}

	// TODO: Create a more generic method instead of this specific one
	@Override
	public URI getDefaultInteractionModel( URI sourceURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		return sparqlTemplate.executeTupleQuery( getDefaultInteractionModelQuery, bindings, queryResult -> {
			if ( ! queryResult.hasNext() ) return null;
			else return ValueUtil.getURI( queryResult.next().getBinding( "dim" ).getValue() );
		} );
	}

	@Override
	public DateTime touch( URI sourceURI ) {
		DateTime now = DateTime.now();
		return touch( sourceURI, now );
	}

	@Override
	public DateTime touch( URI sourceURI, DateTime modified ) {
		resourceRepository.remove( sourceURI, RDFSourceDescription.Property.MODIFIED );
		resourceRepository.add( sourceURI, RDFSourceDescription.Property.MODIFIED.getURI(), modified );
		return modified;
	}

	@Override
	public void createAccessPoint( URI sourceURI, AccessPoint accessPoint ) {
		documentRepository.addDocument( accessPoint.getDocument() );
		addAccessPoint( sourceURI, accessPoint );
	}

	private void addAccessPoint( URI sourceURI, AccessPoint accessPoint ) {
		connectionTemplate.write(
			connection -> connection.add( sourceURI, RDFSourceDescription.Property.ACCESS_POINT.getURI(), accessPoint.getURI(), sourceURI )
		);
	}

	@Override
	public void update( RDFSource source ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replace( RDFSource source ) {
		documentRepository.update( source.getDocument() );
	}

	static {
		isQuery = "ASK { ?resource " + LESS_THAN + RDF.TYPE + MORE_THAN + " ?rdfType." + "${values} }";
	}

	@Override
	public boolean is( URI resourceURI, RDFNodeEnum type ) {
		Map<String, Value> bindings = new LinkedHashMap<>();
		bindings.put( "resource", resourceURI );

		Map<String, String> values = new HashMap<>();
		values.put( "values", SPARQLUtil.assignVar( "rdfType", type ) );
		StrSubstitutor sub = new StrSubstitutor( values, "${", "}" );

		return sparqlTemplate.executeBooleanQuery( sub.replace( isQuery ), bindings );
	}

	private static final String deleteWithChildrenQuery;

	static {
		deleteWithChildrenQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?sourceURI {" + NEW_LINE +
			TAB + TAB + "?sourceSubject ?sourcePredicate ?sourceObject" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?childGraph {" + NEW_LINE +
			TAB + TAB + "?childSubject ?childPredicate ?childObject" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			// DELETE Source's document
			TAB + "GRAPH ?sourceURI {" + NEW_LINE +
			TAB + TAB + "?sourceSubject ?sourcePredicate ?sourceObject" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "OPTIONAL {" + NEW_LINE +
			// DELETE Children Graphs
			TAB + TAB + "GRAPH ?childGraph {" + NEW_LINE +
			TAB + TAB + TAB + "?childSubject ?childPredicate ?childObject" + NEW_LINE +
			TAB + TAB + "}" + NEW_LINE +
			TAB + TAB + "FILTER( STRSTARTS( STR(?childGraph), STR(?sourceURI) ) )" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public void delete( URI sourceURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		sparqlTemplate.executeUpdate( deleteWithChildrenQuery, bindings );
	}

	private static final String deleteOcurrencesIncludingChildrenQuery;

	static {
		deleteOcurrencesIncludingChildrenQuery = "" +
			"DELETE {" + NEW_LINE +
			TAB + "GRAPH ?graph {" + NEW_LINE +
			TAB + TAB + "?subject ?predicate ?object" + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"} WHERE {" + NEW_LINE +
			TAB + "GRAPH ?graph {" + NEW_LINE +
			TAB + TAB + "?subject ?predicate ?object" + NEW_LINE +
			TAB + TAB + "FILTER( " + NEW_LINE +
			TAB + TAB + TAB + "STRSTARTS( str(?predicate), str(?sourceURI) )" + NEW_LINE +
			TAB + TAB + TAB + " || " + NEW_LINE +
			TAB + TAB + TAB + "( isURI(?object) && STRSTARTS( str(?object), str(?sourceURI) ) )" + NEW_LINE +
			TAB + TAB + ")" + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}"
		;
	}

	@Override
	public void deleteOccurrences( URI sourceURI, boolean includeChildrens ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", sourceURI );
		if ( includeChildrens ) sparqlTemplate.executeUpdate( deleteOcurrencesIncludingChildrenQuery, bindings );
		else throw new NotImplementedException();
	}

}
