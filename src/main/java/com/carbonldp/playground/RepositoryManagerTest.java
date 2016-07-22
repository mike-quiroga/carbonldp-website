package com.carbonldp.playground;

import com.carbonldp.AbstractComponent;
import com.carbonldp.repository.security.SecuredNativeStoreConfig;
import com.carbonldp.repository.security.SecuredNativeStoreFactory;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.config.SailRegistry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author MiguelAraCo
 * @since 0.28.0-ALPHA
 */
public class RepositoryManagerTest extends AbstractComponent {
	public static void main( String[] args ) {
		RepositoryManagerTest test = new RepositoryManagerTest();
		try {
			test.run();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	public void run() throws Exception {
		String modelString = "" +
			"<http://example.org/resource-1> <http://example.org/ns#property> <http://example.org/resource-1>." +
			"<http://example.org/resource-2> <http://example.org/ns#property> <http://example.org/resource-2>." +
			"<http://example.org/resource-3> <http://example.org/ns#property> <http://example.org/resource-3>.";

		AbstractModel model = readModel( modelString, RDFFormat.TRIG );

		String query = "" +
			"SELECT" +
			"	?s ?p ?o " +
			"WHERE { " +
			"	?s ?p ?o " +
			"}";

		SailRegistry.getInstance().add( new SecuredNativeStoreFactory() );

		RepositoryManager manager = new LocalRepositoryManager( new File( "/opt/carbon-test" ) );
		if ( ! manager.isInitialized() ) manager.initialize();

		if ( ! manager.hasRepositoryConfig( "test" ) ) registerRepositoryConfig( manager );

		Repository repository = manager.getRepository( "test" );
		RepositoryConnection connection = repository.getConnection();

		connection.add( model );

		TupleQuery tupleQuery = connection.prepareTupleQuery( QueryLanguage.SPARQL, query );
		TupleQueryResult queryResult = tupleQuery.evaluate();

		while ( queryResult.hasNext() ) {
			BindingSet bindingSet = queryResult.next();

			for ( Binding binding : bindingSet ) {
				LOG.debug( binding.getName() + ": " + binding.getValue().stringValue() );
			}
		}
	}

	private void registerRepositoryConfig( RepositoryManager manager ) throws Exception {
		SailImplConfig sailConfig = new SecuredNativeStoreConfig();
		RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig( sailConfig );

		RepositoryConfig repositoryConfig = new RepositoryConfig( "test", repositoryTypeSpec );

		manager.addRepositoryConfig( repositoryConfig );

		Repository repository = manager.getRepository( "test" );
		if ( ! repository.isInitialized() ) repository.initialize();
	}

	private AbstractModel readModel( String modelString, RDFFormat format ) throws Exception {

		InputStream inputStream = new ByteArrayInputStream( modelString.getBytes( StandardCharsets.UTF_8 ) );

		RDFParser parser = Rio.createParser( format );
		AbstractModel model = new LinkedHashModel();

		parser.setRDFHandler( new StatementCollector( model ) );
		parser.parse( inputStream, "" );

		return model;
	}
}
