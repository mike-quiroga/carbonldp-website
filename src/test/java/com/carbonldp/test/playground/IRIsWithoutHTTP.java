package com.carbonldp.test.playground;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class IRIsWithoutHTTP {

	//@Test
	public void Test() {
		Repository repo = new SailRepository( new MemoryStore() );
		IRI IRI = SimpleValueFactory.getInstance().createIRI( "://www.example.org/" );
		try {
			repo.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		try {
			RepositoryConnection connection = repo.getConnection();

			connection.add( IRI, IRI, IRI );
			connection.close();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

	}
}
