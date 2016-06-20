package com.carbonldp.sparql;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.security.RequestDomainAccessGranter;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.*;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Transactional
public class SesameSPARQLService extends AbstractSesameRepository implements SPARQLService {

	public SesameSPARQLService( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public SPARQLResult executeSPARQLQuery( String queryString, IRI targetIRI ) {
		ParsedQuery query;

		SPARQLResult sparqlResult;
		try {
			query = QueryParserUtil.parseQuery( QueryLanguage.SPARQL, queryString, targetIRI.stringValue() );
		} catch ( MalformedQueryException e ) {
			throw new BadRequestException( 0x2401 );
		}
		if ( query instanceof ParsedBooleanQuery ) {// ask query
			sparqlResult = executeSPARQLBooleanQuery( queryString );
		} else if ( query instanceof ParsedTupleQuery ) {// select query
			sparqlResult = executeSPARQLTupleQuery( queryString );
		} else if ( query instanceof ParsedGraphQuery ) {// construct or describe query
			sparqlResult = executeSPARQLGraphedQuery( queryString );
		} else {
			throw new BadRequestException( 0x2401 );
		}

		return sparqlResult;
	}

	@Override
	public void executeSPARQLUpdate( String sparqlUpdate, IRI targetIRI ) {
		sparqlTemplate.executeUpdate( sparqlUpdate, new HashMap<>() );
	}

	private SPARQLResult executeSPARQLBooleanQuery( String queryString ) {
		return SecuredRepositoryTemplate.execute( ( SecuredRepositoryTemplate template ) -> {
			template.setFirstAccessGranters( new RequestDomainAccessGranter() );
			return new SPARQLBooleanResult( sparqlTemplate.executeBooleanQuery( queryString ) );
		} );
	}

	private SPARQLResult executeSPARQLTupleQuery( String queryString ) {
		return SecuredRepositoryTemplate.execute( ( SecuredRepositoryTemplate template ) -> {
			template.setFirstAccessGranters( new RequestDomainAccessGranter() );
			return new SPARQLTupleResult( sparqlTemplate.executeTupleQuery( queryString, InMemoryTupleQueryResult::from ) );
		} );
	}

	private SPARQLResult executeSPARQLGraphedQuery( String queryString ) {
		return SecuredRepositoryTemplate.execute( ( SecuredRepositoryTemplate template ) -> {
			template.setFirstAccessGranters( new RequestDomainAccessGranter() );
			return new SPARQLGraphResult( sparqlTemplate.executeGraphQuery( queryString, InMemoryGraphQueryResult::from ) );
		} );
	}
}
