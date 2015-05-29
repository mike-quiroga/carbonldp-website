package com.carbonldp.test.playground;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class URIsWithoutHTTP {

	//@Test
	public void Test() {
		Repository repo = new SailRepository( new MemoryStore() );
		URI URI = new URIImpl( "://www.example.org/" );
		try {
			repo.initialize();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
		try {
			RepositoryConnection connection = repo.getConnection();

			connection.add( URI, URI, URI );
			connection.close();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

	}
}
