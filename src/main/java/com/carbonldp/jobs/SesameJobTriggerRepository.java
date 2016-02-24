package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.authorization.acl.SesameACLService;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import com.carbonldp.jobs.TriggerDescription.Type;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

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

	@Override
	public boolean supports( Type triggerType ) {
		return triggerType == Type.JOB_TRIGGER;
	}

	@Override
	public void executeTrigger( URI triggerURI ) {
		URI jobURI = getJobURI( triggerURI );
		addJobToQueue( jobURI );
		changeJobStatus( jobURI );
	}

	private void addJobToQueue( URI jobURI ) {
		URI appURI = getAppURI( jobURI );
		URI appJobsQueue = new URIImpl( appURI.stringValue() + "#" + Vars.getInstance().getJobsQueue() );

		if ( isQueueEmpty( appURI, appJobsQueue ) ) {
			try {
				connectionFactory.getConnection().add( appJobsQueue, RDF.FIRST, jobURI, appURI );
				connectionFactory.getConnection().add( appJobsQueue, RDF.REST, RDF.NIL, appURI );
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}

		} else {
			addJobToTheEnd( jobURI, appURI );
		}

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

	private boolean isQueueEmpty( URI appURI, URI appJobsQueue ) {
		try {
			return ( ! connectionFactory.getConnection().hasStatement( appJobsQueue, RDF.FIRST, null, false, appURI ) );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}
	}

	private void changeJobStatus( URI jobURI ) {
		try {
			connectionFactory.getConnection().remove( jobURI, JobDescription.Property.JOB_STATUS.getURI(), null, jobURI );
			connectionFactory.getConnection().add( jobURI, JobDescription.Property.JOB_STATUS.getURI(), JobDescription.JobStatus.QUEUED.getURI(), jobURI );
		} catch ( RepositoryException e ) {
			throw new RuntimeException( e );
		}

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

	private URI getAppURI( URI jobURI ) {
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
}
