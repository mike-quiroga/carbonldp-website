package com.carbonldp.repository.services;

import static com.carbonldp.commons.Consts.NEW_LINE;
import static com.carbonldp.commons.Consts.TAB;
import info.aduna.iteration.Iterations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.AbstractService;
import com.carbonldp.commons.models.RDFDocument;
import com.carbonldp.commons.utils.RDFDocumentUtil;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.repository.RepositoryRuntimeException;

//@Service("rdfDocumentService")
public class RDFDocumentService extends AbstractService {

	private static final String documentExistsQuery;
	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("ASK {").append(NEW_LINE)
			.append(TAB).append("GRAPH ?graphName {").append(NEW_LINE)
			.append(TAB).append(TAB).append("?s ?p ?o").append(NEW_LINE)
			.append(TAB).append("}").append(NEW_LINE)
			.append("}")
		;
		//@formatter:on
		documentExistsQuery = queryBuilder.toString();
	}

	@Transactional
	public boolean documentExists(URI documentURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		BooleanQuery query;
		try {
			query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, documentExistsQuery);
		} catch (MalformedQueryException e) {
			throw new StupidityException(e);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx documentExists() > A query couldn't be prepared on the repository.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}

		query.setBinding("?graphName", documentURI);

		try {
			return query.evaluate();
		} catch (QueryEvaluationException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx documentExists() > A query couldn't be evaluated on the repository.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}

	@Transactional
	public RDFDocument getDocument(URI documentURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		RepositoryResult<Statement> statementsIterator;
		try {
			statementsIterator = connection.getStatements(null, null, null, false, documentURI);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx getDocument() > The statements couldn't be retrieved.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}

		AbstractModel model = retrieveModel(statementsIterator);
		return new RDFDocument(model, documentURI);
	}

	@Transactional
	public Set<RDFDocument> getDocuments(Collection<? extends URI> documentURIs) {
		RepositoryConnection connection = connectionFactory.getConnection();

		URI[] contexts = documentURIs.toArray(new URI[documentURIs.size()]);
		RepositoryResult<Statement> statementsIterator;
		try {
			statementsIterator = connection.getStatements(null, null, null, false, contexts);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx getDocument() > The statements couldn't be retrieved.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}

		AbstractModel model = retrieveModel(statementsIterator);

		return RDFDocumentUtil.getDocuments(model, documentURIs);
	}

	@Transactional
	public void addDocument(RDFDocument document) {
		RepositoryConnection connection = connectionFactory.getConnection();

		try {
			connection.add(document);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx addDocument() > The statements couldn't be added.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}

	@Transactional
	public void addDocuments(Collection<RDFDocument> documents) {
		for (RDFDocument document : documents) {
			addDocument(document);
		}
	}

	@Transactional
	public void deleteDocument(URI documentURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		// Remove ambiguity
		Resource subject = null;
		try {
			connection.remove(subject, null, null, documentURI);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx deleteDocument() > The document's statements couldn't be removed.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}

	@Transactional
	public void deleteDocuments(Collection<URI> documentURIs) {
		RepositoryConnection connection = connectionFactory.getConnection();

		URI[] contexts = documentURIs.toArray(new URI[documentURIs.size()]);
		// Remove ambiguity
		Resource subject = null;
		try {
			connection.remove(subject, null, null, contexts);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx deleteDocuments() > The documents' statements couldn't be removed.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}

	private AbstractModel retrieveModel(RepositoryResult<Statement> statementsIterator) {
		Set<Statement> statements = new HashSet<Statement>();
		try {
			Iterations.addAll(statementsIterator, statements);
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx retrieveStatements() > The statements couldn't be retrieved.");
			}
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}

		AbstractModel model = new TreeModel(statements);

		return model;
	}
}
