package com.carbonldp.repository;

import info.aduna.iteration.Iterations;

import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.commons.descriptions.RDFResourceDescription;
import com.carbonldp.commons.models.RDFResource;
import com.carbonldp.repository.txn.RepositoryRuntimeException;

@Transactional
public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	public SesameRDFResourceRepository(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	@Override
	public Set<URI> getTypes(URI resourceURI) {
		RepositoryConnection connection = connectionFactory.getConnection();
		AbstractModel model = new LinkedHashModel();
		for (URI predicate : RDFResourceDescription.Property.TYPE.getURIs()) {
			RepositoryResult<Statement> statements;
			try {
				statements = connection.getStatements(resourceURI, predicate, null, false, resourceURI);
				model.addAll(Iterations.asSet(statements));
			} catch (RepositoryException e) {
				// TODO: Add error code
				throw new RepositoryRuntimeException(e);
			}

		}
		RDFResource resource = new RDFResource(model, resourceURI);
		return resource.getTypes();
	}

}
