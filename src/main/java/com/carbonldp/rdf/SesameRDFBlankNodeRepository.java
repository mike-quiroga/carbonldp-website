package com.carbonldp.rdf;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */

@Transactional
public class SesameRDFBlankNodeRepository extends SesameRDFNodeRepository<BNode> implements RDFBlankNodeRepository {

	public SesameRDFBlankNodeRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public BNode get( String identifier, IRI documentIRI ) {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		return (BNode) connectionTemplate.readStatements(
			connection -> connection.getStatements( null, RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI(), valueFactory.createLiteral( identifier ), false, documentIRI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getSubject();
			}
		);
	}
}
