package com.carbonldp.ldp.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;

public abstract class AbstractTypedContainerService extends AbstractSesameLDPService implements TypedContainerService {

	public AbstractTypedContainerService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository) {
		super(connectionFactory, resourceRepository, documentRepository);
	}

	protected Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, String findMembers_query) {
		RepositoryConnection connection = connectionFactory.getConnection();
		String queryString = String.format(findMembers_query, sparqlSelector);
		TupleQuery query;
		try {
			query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		} catch (MalformedQueryException e) {
			throw new StupidityException(e);
		} catch (RepositoryException e) {
			// TODO: Add error code
			throw new RepositoryRuntimeException(e);
		}

		// TODO: Make ?containerURI a constant
		query.setBinding("?containerURI", containerURI);

		if ( bindings != null ) {
			for (Entry<String, Value> binding : bindings.entrySet()) {
				query.setBinding(binding.getKey(), binding.getValue());
			}
		}

		Set<URI> members = new HashSet<URI>();
		try {
			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value member = bindingSet.getValue("members");
				if ( ValueUtil.isURI(member) ) members.add(ValueUtil.getURI(member));
			}

		} catch (QueryEvaluationException e) {
			// TODO: Add error code
			throw new RepositoryRuntimeException(e);
		}

		return members;

	}

	protected Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, String filterMembers_query) {
		Set<URI> members = new HashSet<URI>();
		if ( possibleMemberURIs.isEmpty() ) return members;

		RepositoryConnection connection = connectionFactory.getConnection();

		String queryString = String.format(filterMembers_query, SPARQLUtil.generateFilterInPlaceHolder("?members", possibleMemberURIs.size()));
		TupleQuery query;
		try {
			query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		} catch (MalformedQueryException e) {
			throw new StupidityException(e);
		} catch (RepositoryException e) {
			// TODO: Add error code
			throw new RepositoryRuntimeException(e);
		}

		// TODO: Make ?containerURI a constant
		query.setBinding("?containerURI", containerURI);

		SPARQLUtil.populateSequentialPlaceholders(query, possibleMemberURIs);

		try {
			TupleQueryResult result = query.evaluate();

			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value member = bindingSet.getValue("members");
				if ( ValueUtil.isURI(member) ) members.add(ValueUtil.getURI(member));
			}

		} catch (QueryEvaluationException e) {
			// TODO: Add error code
			throw new RepositoryRuntimeException(e);
		}

		return members;
	}
}
