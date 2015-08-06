package com.carbonldp.sparql;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.web.exceptions.BadRequestException;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.*;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesameSPARQLService extends AbstractSesameRepository implements SPARQLService {
	//TODO: check about RDF dataset with the URL
	private SPARQLResult sparqlResult;

	public SesameSPARQLService( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public SPARQLResult executeSPARQLQuery( String queryString, URI targetURI ) {
		ParsedQuery query;

		try {
			query = QueryParserUtil.parseQuery( QueryLanguage.SPARQL, queryString, targetURI.stringValue() );
		} catch ( MalformedQueryException e ) {
			throw new BadRequestException( "Query submitted was malformed, nested Exception: " + e.toString() );
		}
		if ( query instanceof ParsedBooleanQuery ) {// ask query
			sparqlResult = executeSPARQLBooleanQuery( queryString );
		} else if ( query instanceof ParsedTupleQuery ) {// select query
			sparqlResult = executeSPARQLTupleQuery( queryString );
		} else if ( query instanceof ParsedGraphQuery ) {// construct or describe query
			sparqlResult = executeSPARQLGraphedQuery( queryString );
		} else {
			throw new BadRequestException( "Query submitted was malformed" );
		}

		return sparqlResult;
	}

	private SPARQLResult executeSPARQLBooleanQuery( String queryString ) {
		return new SPARQLBooleanResult( sparqlTemplate.executeBooleanQuery( queryString ) );

	}

	private SPARQLResult executeSPARQLTupleQuery( String queryString ) {
		return new SPARQLTupleResult(
			sparqlTemplate.executeTupleQuery( queryString, queryResult -> InMemoryTupleQueryResult.from( queryResult )
			) );
	}

	private SPARQLResult executeSPARQLGraphedQuery( String queryString ) {
		return new SPARQLGraphResult(
			sparqlTemplate.executeGraphQuery( queryString, queryResult -> queryResult ) );
	}

}
