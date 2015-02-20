package com.carbonldp.ldp.services;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.exceptions.MalformedDataException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;

public abstract class AbstractAccessPointService extends AbstractTypedContainerService {

	public AbstractAccessPointService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository) {
		super(connectionFactory, resourceRepository, documentRepository);
	}

	private static final String getMembershipResource_query;
	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("SELECT ?membershipResource WHERE {").append(NEW_LINE)
			.append(TAB).append("GRAPH ?containerURI {").append(NEW_LINE)
			.append(TAB).append(TAB).append(RDFNodeUtil.generatePredicateStatement("?containerURI", "?membershipResource", ContainerDescription.Property.MEMBERSHIP_RESOURCE)).append(NEW_LINE)
			.append(TAB).append(TAB).append("FILTER(isURI(?membershipResource)).").append(NEW_LINE)
			.append(TAB).append("}").append(NEW_LINE)
			.append("}").append(NEW_LINE)
			.append("LIMIT 1")
		;
		//@formatter:on
		getMembershipResource_query = queryBuilder.toString();
	}

	// TODO: Create a more generic method instead of this specific one
	protected URI getMembershipResource(URI containerURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		TupleQuery query;
		try {
			query = connection.prepareTupleQuery(QueryLanguage.SPARQL, getMembershipResource_query);
		} catch (RepositoryException e) {
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		} catch (MalformedQueryException e) {
			throw new StupidityException(e);
		}

		query.setBinding("containerURI", containerURI);

		try {
			TupleQueryResult result = query.evaluate();
			if ( ! result.hasNext() ) {
				// TODO: Add error number
				throw new MalformedDataException(0);
			} else return ValueUtil.getURI(result.next().getBinding("membershipResource").getValue());
		} catch (QueryEvaluationException e) {
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}
}
