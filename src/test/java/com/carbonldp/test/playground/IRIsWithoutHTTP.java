package com.carbonldp.test.playground;

import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

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
