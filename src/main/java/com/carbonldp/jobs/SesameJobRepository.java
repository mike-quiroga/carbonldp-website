package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameJobRepository extends AbstractSesameLDPRepository implements JobRepository {

	public SesameJobRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String getExecutionQueueLocationQuery;

	static {
		getExecutionQueueLocationQuery = "" +
			"SELECT ?executionQueueLocationURI" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?jobURI {" + NEW_LINE +
			TAB + TAB + "?jobURI <" + JobDescription.Property.EXECUTION_QUEUE_LOCATION.getURI().stringValue() + "> ?executionQueueLocationURI" + NEW_LINE +
			TAB + "}." + NEW_LINE +
			"}";
	}

	@Override
	public URI getExecutionQueueLocation( URI jobURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "jobURI", jobURI );

		return sparqlTemplate.executeTupleQuery( getExecutionQueueLocationQuery, bindings, queryResult -> {
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "executionQueueLocationURI" );
				if ( ValueUtil.isURI( member ) ) return ValueUtil.getURI( member );
			}
			throw new RuntimeException( "there is not an app related" );
		} );
	}

}
