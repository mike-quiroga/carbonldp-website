package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */

@Transactional
public class SesameRDFBlankNodeRepository extends SesameRDFNodeRepository<BNode> implements RDFBlankNodeRepository {

	public SesameRDFBlankNodeRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public BNode get( String identifier, URI documentURI ) {
		ValueFactory valueFactory = new ValueFactoryImpl();
		return (BNode) connectionTemplate.readStatements(
			connection -> connection.getStatements( null, RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getURI(), valueFactory.createLiteral( identifier ), false, documentURI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getSubject();
			}
		);
	}
}
