package com.carbonldp.ldp.services;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.models.Container;
import com.carbonldp.models.ContainerFactory;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.DocumentGraphQueryResultHandler;
import com.carbonldp.repository.EmptyConnectionActionCallback;
import com.carbonldp.repository.GraphQueryResultHandler;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.NotImplementedException;

@Transactional
public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {

	private final RDFSourceService sourceService;
	private final List<TypedContainerService> typedContainerServices;

	public SesameContainerService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository, RDFSourceService sourceService, List<TypedContainerService> typedContainerServices) {
		super(connectionFactory, resourceRepository, documentRepository);
		this.sourceService = sourceService;
		this.typedContainerServices = typedContainerServices;
	}

	@Override
	public boolean isMember(URI containerURI, URI possibleMemberURI) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		return isMember(containerURI, possibleMemberURI, containerType);
	}

	@Override
	public boolean isMember(URI containerURI, URI possibleMemberURI, Type containerType) {
		return getService(containerType).isMember(containerURI, possibleMemberURI);
	}

	@Override
	public Container get(URI containerURI, Set<ContainerRetrievalPreference> preferences) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		Container container = ContainerFactory.get(containerURI, containerType);
		for (ContainerRetrievalPreference preference : preferences) {
			switch (preference) {
				case CONTAINER_PROPERTIES:
					container.getBaseModel().addAll(getProperties(containerURI));
					break;
				case CONTAINMENT_TRIPLES:
					container.getBaseModel().addAll(getContainmentTriples(containerURI));
					break;
				case CONTAINED_RESOURCES:
					throw new NotImplementedException();
				case MEMBERSHIP_TRIPLES:
					container.getBaseModel().addAll(getMembershipTriples(containerURI));
					break;
				case MEMBER_RESOURCES:
					throw new NotImplementedException();
				default:
					throw new IllegalStateException();

			}
		}
		return container;
	}

	@Override
	public Type getContainerType(URI containerURI) {
		Set<URI> resourceTypes = resourceRepository.getTypes(containerURI);
		for (URI resourceType : resourceTypes) {
			Type containerType = RDFNodeUtil.findByURI(resourceType, ContainerDescription.Type.class);
			if ( containerType != null ) return containerType;
		}
		return null;
	}

	@Override
	public Set<Statement> getProperties(URI containerURI) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		return getProperties(containerURI, containerType);
	}

	@Override
	public Set<Statement> getProperties(URI containerURI, Type containerType) {
		return getService(containerType).getProperties(containerURI);
	}

	private static final String getContainmentTriples_query;
	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("CONSTRUCT {").append(NEW_LINE)
			.append(TAB).append("?containerURI ?p ?o").append(NEW_LINE)
			.append("} WHERE {").append(NEW_LINE)
			.append(TAB).append("GRAPH ?containerURI {").append(NEW_LINE)
			.append(TAB).append(TAB).append(RDFNodeUtil.generatePredicateStatement("?containerURI", "?p", "?o", ContainerDescription.Property.CONTAINS)).append(NEW_LINE)
			.append(TAB).append("}").append(NEW_LINE)
			.append("}")
		;
		//@formatter:on
		getContainmentTriples_query = queryBuilder.toString();
	}

	@Override
	public Set<Statement> getContainmentTriples(URI containerURI) {
		RepositoryConnection connection = connectionFactory.getConnection();

		GraphQuery query;
		try {
			query = connection.prepareGraphQuery(QueryLanguage.SPARQL, getContainmentTriples_query);
		} catch (RepositoryException e) {
			throw new RepositoryRuntimeException(e);
		} catch (MalformedQueryException e) {
			throw new StupidityException(e);
		}

		query.setBinding("containerURI", containerURI);

		Set<Statement> statements = new HashSet<Statement>();
		GraphQueryResultHandler handler = new DocumentGraphQueryResultHandler(statements);
		handler.handleQuery(query);

		return statements;
	}

	@Override
	public Set<Statement> getMembershipTriples(URI containerURI) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		return getMembershipTriples(containerURI, containerType);
	}

	@Override
	public Set<Statement> getMembershipTriples(URI containerURI, Type containerType) {
		return getService(containerType).getMembershipTriples(containerURI);
	}

	@Override
	public Set<ContainerRetrievalPreference> getRetrievalPreferences(URI containerURI) {
		Set<URI> preferenceURIs = resourceRepository.getURIs(containerURI, ContainerDescription.Property.DEFAULT_RETRIEVE_PREFERENCE);
		return RDFNodeUtil.findByURIs(preferenceURIs, ContainerRetrievalPreference.class);
	}

	@Override
	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		return findMembers(containerURI, sparqlSelector, bindings, containerType);
	}

	@Override
	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, Type containerType) {
		return getService(containerType).findMembers(containerURI, sparqlSelector, bindings);
	}

	@Override
	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		return filterMembers(containerURI, possibleMemberURIs, containerType);
	}

	@Override
	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, Type containerType) {
		return getService(containerType).filterMembers(containerURI, possibleMemberURIs);
	}

	@Override
	public void createChild(URI containerURI, RDFSource child) {
		Type containerType = getContainerType(containerURI);
		if ( containerType == null ) throw new IllegalStateException("The resource isn't a container.");

		createChild(containerURI, child, containerType);
	}

	@Override
	public void createChild(URI containerURI, RDFSource child, Type containerType) {
		addContainedResource(containerURI, child.getURI());
		child = getService(containerType).addMember(containerURI, child);
		documentRepository.addDocument(child.getDocument());
	}

	@Override
	public void addMember(URI containerURI, RDFSource member) {
		// TODO
	}

	private void addContainedResource(final URI containerURI, final URI resourceURI) {
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(containerURI, ContainerDescription.Property.CONTAINS.getURI(), resourceURI, containerURI);
			}
		});
	}

	private TypedContainerService getService(Type containerType) {
		for (TypedContainerService service : typedContainerServices) {
			if ( service.supports(containerType) ) return service;
		}
		return null;
	}
}
