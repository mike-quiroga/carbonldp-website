package com.carbonldp.repository.txn;

import org.eclipse.rdf4j.IsolationLevel;
import org.eclipse.rdf4j.common.iteration.Iteration;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.*;
import org.eclipse.rdf4j.rio.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class ReadOnlyRepositoryConnection implements RepositoryConnection {

	private final RepositoryConnection connection;

	public ReadOnlyRepositoryConnection( RepositoryConnection connection ) {
		this.connection = connection;
	}

	@Override
	public Repository getRepository() {
		return connection.getRepository();
	}

	@Override
	public void setParserConfig( ParserConfig config ) {
		connection.setParserConfig( config );
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
	public Query prepareQuery( QueryLanguage ql, String query ) throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery( ql, query );
	}

	@Override
	public Query prepareQuery( QueryLanguage ql, String query, String baseIRI ) throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery( ql, query, baseIRI );
	}

	@Override
	public TupleQuery prepareTupleQuery( QueryLanguage ql, String query ) throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery( ql, query );
	}

	@Override
	public TupleQuery prepareTupleQuery( QueryLanguage ql, String query, String baseIRI ) throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery( ql, query, baseIRI );
	}

	@Override
	public GraphQuery prepareGraphQuery( QueryLanguage ql, String query ) throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery( ql, query );
	}

	@Override
	public GraphQuery prepareGraphQuery( QueryLanguage ql, String query, String baseIRI ) throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery( ql, query, baseIRI );
	}

	@Override
	public BooleanQuery prepareBooleanQuery( QueryLanguage ql, String query ) throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery( ql, query );
	}

	@Override
	public BooleanQuery prepareBooleanQuery( QueryLanguage ql, String query, String baseIRI ) throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery( ql, query, baseIRI );
	}

	@Override
	public Update prepareUpdate( QueryLanguage ql, String update ) throws RepositoryException, MalformedQueryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public Update prepareUpdate( QueryLanguage ql, String update, String baseIRI ) throws RepositoryException, MalformedQueryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public RepositoryResult<Resource> getContextIDs() throws RepositoryException {
		return connection.getContextIDs();
	}

	@Override
	public RepositoryResult<Statement> getStatements( Resource subj, IRI pred, Value obj, boolean includeInferred, Resource... contexts )
		throws RepositoryException {
		return connection.getStatements( subj, pred, obj, includeInferred, contexts );
	}

	@Override
	public boolean hasStatement( Resource subj, IRI pred, Value obj, boolean includeInferred, Resource... contexts ) throws RepositoryException {
		return connection.hasStatement( subj, pred, obj, includeInferred, contexts );
	}

	@Override
	public boolean hasStatement( Statement st, boolean includeInferred, Resource... contexts ) throws RepositoryException {
		return connection.hasStatement( st, includeInferred, contexts );
	}

	@Override
	public void exportStatements( Resource subj, IRI pred, Value obj, boolean includeInferred, RDFHandler handler, Resource... contexts )
		throws RepositoryException, RDFHandlerException {
		connection.exportStatements( subj, pred, obj, includeInferred, handler, contexts );
	}

	@Override
	public void export( RDFHandler handler, Resource... contexts ) throws RepositoryException, RDFHandlerException {
		connection.export( handler, contexts );
	}

	@Override
	public long size( Resource... contexts ) throws RepositoryException {
		return connection.size( contexts );
	}

	@Override
	public boolean isEmpty() throws RepositoryException {
		return connection.isEmpty();
	}

	@Deprecated
	@Override
	public void setAutoCommit( boolean autoCommit ) throws RepositoryException {
		connection.setAutoCommit( autoCommit );
	}

	@Deprecated
	@Override
	public boolean isAutoCommit() throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public boolean isActive() throws UnknownTransactionStateException, RepositoryException {
		return connection.isActive();
	}

	@Override
	public void setIsolationLevel( IsolationLevel level ) throws IllegalStateException {

	}

	@Override
	public IsolationLevel getIsolationLevel() {
		return null;
	}

	@Override
	public void begin() throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void begin( IsolationLevel level ) throws RepositoryException {

	}

	@Override
	public void commit() throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void rollback() throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( InputStream in, String baseIRI, RDFFormat dataFormat, Resource... contexts ) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( Reader reader, String baseIRI, RDFFormat dataFormat, Resource... contexts ) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( URL url, String baseIRI, RDFFormat dataFormat, Resource... contexts ) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( File file, String baseIRI, RDFFormat dataFormat, Resource... contexts ) throws IOException, RDFParseException, RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( Resource subject, IRI predicate, Value object, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( Statement st, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void add( Iterable<? extends Statement> statements, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public <E extends Exception> void add( Iteration<? extends Statement, E> statements, Resource... contexts ) throws RepositoryException, E {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void remove( Resource subject, IRI predicate, Value object, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void remove( Statement st, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void remove( Iterable<? extends Statement> statements, Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public <E extends Exception> void remove( Iteration<? extends Statement, E> statements, Resource... contexts ) throws RepositoryException, E {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void clear( Resource... contexts ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public RepositoryResult<Namespace> getNamespaces() throws RepositoryException {
		return connection.getNamespaces();
	}

	@Override
	public String getNamespace( String prefix ) throws RepositoryException {
		return connection.getNamespace( prefix );
	}

	@Override
	public void setNamespace( String prefix, String name ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void removeNamespace( String prefix ) throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

	@Override
	public void clearNamespaces() throws RepositoryException {
		throw new RepositoryRuntimeException( 0x0001 );
	}

}
