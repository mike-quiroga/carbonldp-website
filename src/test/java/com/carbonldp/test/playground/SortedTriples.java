package com.carbonldp.test.playground;

import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.test.AbstractIT;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test( enabled = false )
public class SortedTriples extends AbstractIT {

	@Autowired
	private RDFResourceRepository resourceRepository;

	IRI subj = SimpleValueFactory.getInstance().createIRI( "https://local.carbonldp.com/apps/test-blog/posts/post-1" );

	@Test
	public void test() {
		IRI pred = SimpleValueFactory.getInstance().createIRI( "http://example.org/ns#example" );
		Value obj = SimpleValueFactory.getInstance().createIRI( "https://local.carbonldp.com/apps/test-blog/posts/post-1/sampleIRI" );
		resourceRepository.add( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), true );

		int i = resourceRepository.getInteger( subj, pred );
		assertEquals( i, 1 );

		resourceRepository.remove( subj, pred, obj );
		assertEquals( resourceRepository.contains( subj, pred, obj ), false );
	}
}