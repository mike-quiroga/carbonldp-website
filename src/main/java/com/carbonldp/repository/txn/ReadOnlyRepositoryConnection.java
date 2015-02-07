package com.carbonldp.repository.txn;

import info.aduna.iteration.Iteration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.UnknownTransactionStateException;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

public class ReadOnlyRepositoryConnection implements RepositoryConnection {

	private final RepositoryConnection connection;

	public ReadOnlyRepositoryConnection(RepositoryConnection connection) {
		this.connection = connection;
	}

	@Override
	public Repository getRepository() {
		return connection.getRepository();
	}

	@Override
	public void setParserConfig(ParserConfig config) {
		connection.setParserConfig(config);
	}

	@Override
	public ParserConfig getParserConfig() {
		return connection.getParserConfig();
	}

	@Override
	public ValueFactory getValueFactory() {
		return connection.getValueFactory();
	}

	@Override
	public boolean isOpen() throws RepositoryException {
		return connection.isOpen();
	}

	@Override
	public void close() throws RepositoryException {
		connection.close();
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery(ql, query);
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery(ql, query, baseURI);
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery(ql, query);
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery(ql, query, baseURI);
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery(ql, query);
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery(ql, query, baseURI);
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery(ql, query);
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query, String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery(ql, query, baseURI);
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update) throws RepositoryException, MalformedQueryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update, String baseURI) throws RepositoryException, MalformedQueryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public RepositoryResult<Resource> getContextIDs() throws RepositoryException {
		return connection.getContextIDs();
	}

	@Override
	public RepositoryResult<Statement> getStatements(Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts)
			throws RepositoryException {
		return connection.getStatements(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts) throws RepositoryException {
		return connection.hasStatement(subj, pred, obj, includeInferred, contexts);
	}

	@Override
	public boolean hasStatement(Statement st, boolean includeInferred, Resource... contexts) throws RepositoryException {
		return connection.hasStatement(st, includeInferred, contexts);
	}

	@Override
	public void exportStatements(Resource subj, URI pred, Value obj, boolean includeInferred, RDFHandler handler, Resource... contexts)
			throws RepositoryException, RDFHandlerException {
		connection.exportStatements(subj, pred, obj, includeInferred, handler, contexts);
	}

	@Override
	public void export(RDFHandler handler, Resource... contexts) throws RepositoryException, RDFHandlerException {
		connection.export(handler, contexts);
	}

	@Override
	public long size(Resource... contexts) throws RepositoryException {
		return connection.size(contexts);
	}

	@Override
	public boolean isEmpty() throws RepositoryException {
		return connection.isEmpty();
	}

	@Deprecated
	@Override
	public void setAutoCommit(boolean autoCommit) throws RepositoryException {
		connection.setAutoCommit(autoCommit);
	}

	@Override
	public boolean isAutoCommit() throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public boolean isActive() throws UnknownTransactionStateException, RepositoryException {
		return connection.isActive();
	}

	@Override
	public void begin() throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void commit() throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void rollback() throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(InputStream in, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(Reader reader, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(URL url, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(File file, String baseURI, RDFFormat dataFormat, Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(Resource subject, URI predicate, Value object, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(Statement st, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void add(Iterable<? extends Statement> statements, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public <E extends Exception> void add(Iteration<? extends Statement, E> statements, Resource... contexts) throws RepositoryException, E {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void remove(Resource subject, URI predicate, Value object, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void remove(Statement st, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void remove(Iterable<? extends Statement> statements, Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public <E extends Exception> void remove(Iteration<? extends Statement, E> statements, Resource... contexts) throws RepositoryException, E {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void clear(Resource... contexts) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public RepositoryResult<Namespace> getNamespaces() throws RepositoryException {
		return connection.getNamespaces();
	}

	@Override
	public String getNamespace(String prefix) throws RepositoryException {
		return connection.getNamespace(prefix);
	}

	@Override
	public void setNamespace(String prefix, String name) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void removeNamespace(String prefix) throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

	@Override
	public void clearNamespaces() throws RepositoryException {
		throw new RepositoryRuntimeException(0x0001);
	}

}
