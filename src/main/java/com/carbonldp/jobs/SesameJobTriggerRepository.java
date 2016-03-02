package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.carbonldp.jobs.TriggerDescription.Type;

import java.util.HashMap;
import java.util.Map;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameJobTriggerRepository extends AbstractSesameLDPRepository implements TypedTriggerRepository {
	public SesameJobTriggerRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	JobRepository jobRepository;

	@Override
	public boolean supports( Type triggerType ) {
		return triggerType == Type.JOB_TRIGGER;
	}

	@Override
	public void executeTrigger( URI triggerURI ) {
		URI jobURI = getJobURI( triggerURI );
		addJobToQueue( jobURI );
		setJobStatusToQueued( jobURI );
	}

	private void addJobToQueue( URI jobURI ) {
		URI appURI = jobRepository.getAppURI( jobURI );
		addJobToTheEnd( jobURI, appURI );

	}

	private void addJobToTheEnd( URI jobURI, URI appURI ) {
		BNode newQueueMemberBNode = connectionFactory.getConnection().getValueFactory().createBNode();
		Resource lastQueueMemberSubject = getLastMemberBNode( appURI );

		try {
			connectionFactory.getConnection().add( newQueueMemberBNode, RDF.FIRST, jobURI, appURI );
			connectionFactory.getConnection().add( newQueueMemberBNode, RDF.REST, RDF.NIL, appURI );
			connectionFactory.getConnection().add( lastQueueMemberSubject, RDF.REST, newQueueMemberBNode, appURI );
			connectionFactory.getConnection().remove( lastQueueMemberSubject, RDF.REST, RDF.NIL, appURI );

		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

	}

	private Resource getLastMemberBNode( URI appURI ) {
		RepositoryResult<Statement> statements;
		try {
			statements = connectionFactory.getConnection().getStatements( null, RDF.REST, RDF.NIL, false, appURI );
			if ( ! statements.hasNext() ) throw new RuntimeException( "queue is malformed" );
			return statements.next().getSubject();
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private void setJobStatusToQueued( URI jobURI ) {
		if ( ! jobRepository.getJobStatus( jobURI ).equals( JobDescription.JobStatus.RUNNING ) )
			jobRepository.changeJobStatus( jobURI, JobDescription.JobStatus.QUEUED );
	}

	private static final String getJobRelatedQuery;

	static {
		getJobRelatedQuery = "" +
			"SELECT ?job WHERE {" + NEW_LINE +
			TAB + "GRAPH ?triggerURI {" + NEW_LINE +
			TAB + TAB + "?triggerURI <" + TriggerDescription.Property.JOB.getURI().stringValue() + "> ?job" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}"
		;
	}

	private URI getJobURI( URI triggerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "triggerURI", triggerURI );

		return sparqlTemplate.executeTupleQuery( getJobRelatedQuery, bindings, queryResult -> {
			if ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "job" );
				if ( ValueUtil.isURI( member ) ) return ValueUtil.getURI( member );
			}
			throw new RuntimeException( "the trigger is not related to any job" );
		} );
	}

	@Autowired
	public void setJobRepository( JobRepository jobRepository ) {
		this.jobRepository = jobRepository;
	}
}
