package com.carbonldp.rdf;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;
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
