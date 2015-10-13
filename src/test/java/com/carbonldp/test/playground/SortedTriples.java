package com.carbonldp.test.playground;

import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SortedTriples extends AbstractIT {

	@Autowired
	private RDFResourceRepository resourceRepository;

	URI subj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1" );

	@Test
	public void test() {
		URI pred = new URIImpl( "http://example.org/ns#example" );
		Value obj = new URIImpl( "http://local.carbonldp.com/apps/test-blog/posts/post-1/sampleURI" );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );

		int i = resourceRepository.getInteger( subj, pred );
		assertEquals( i, 1 );

		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), false );
	}
}
