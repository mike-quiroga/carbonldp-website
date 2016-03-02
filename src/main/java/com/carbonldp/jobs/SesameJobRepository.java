package com.carbonldp.jobs;

import com.carbonldp.jobs.JobDescription.JobStatus;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.HashMap;
import java.util.Map;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameJobRepository extends AbstractSesameLDPRepository implements JobRepository {
	public SesameJobRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String getAppRelatedQuery;

	static {
		getAppRelatedQuery = "" +
			"SELECT ?app WHERE {" + NEW_LINE +
			TAB + "GRAPH ?jobURI {" + NEW_LINE +
			TAB + TAB + "?jobURI <" + JobDescription.Property.APP_RELATED.getURI().stringValue() + "> ?app" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	@Override
	public URI getAppURI( URI jobURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "jobURI", jobURI );

		return sparqlTemplate.executeTupleQuery( getAppRelatedQuery, bindings, queryResult -> {
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "app" );
				if ( ValueUtil.isURI( member ) ) return ValueUtil.getURI( member );
			}
			throw new RuntimeException( "the trigger is not related to any job" );
		} );
	}

	@Override
	public JobStatus getJobStatus( URI jobURI ) {
		RepositoryResult<Statement> statements;
		Statement statement;

		try {
			statements = connectionFactory.getConnection().getStatements( jobURI, JobDescription.Property.JOB_STATUS.getURI(), null, false, jobURI );
			if ( ! statements.hasNext() ) throw new RuntimeException( "job does not have a status" );
			statement = statements.next();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

		Value object = statement.getObject();
		if ( ! ValueUtil.isURI( object ) ) throw new RuntimeException( "job status is an invalid type" );
		URI jobStatusURI = ValueUtil.getURI( object );

		for ( JobStatus jobStatus : JobStatus.values() ) {
			if ( jobStatus.getURI().equals( jobStatusURI ) ) return jobStatus;
		}
		throw new RuntimeException( "invalid job status" );
	}

	@Override
	public void changeJobStatus( URI jobURI, JobStatus jobStatus ) {
		try {
			connectionFactory.getConnection().remove( jobURI, JobDescription.Property.JOB_STATUS.getURI(), null, jobURI );
			connectionFactory.getConnection().add( jobURI, JobDescription.Property.JOB_STATUS.getURI(), jobStatus.getURI(), jobURI );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

	}
}
