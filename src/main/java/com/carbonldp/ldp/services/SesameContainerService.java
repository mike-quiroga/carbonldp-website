package com.carbonldp.ldp.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.descriptions.ContainerDescription;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.RDFNodeUtil;

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
	public Type getContainerType(URI containerURI) {
		Set<URI> resourceTypes = resourceRepository.getTypes(containerURI);
		for (URI resourceType : resourceTypes) {
			Type containerType = RDFNodeUtil.findByURI(resourceType, ContainerDescription.Type.class);
			if ( containerType != null ) return containerType;
		}
		return null;
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

	private void addContainedResource(URI containerURI, URI resourceURI) {
		RepositoryConnection connection = connectionFactory.getConnection();
		try {
			connection.add(containerURI, ContainerDescription.Property.CONTAINS.getURI(), resourceURI, containerURI);
		} catch (RepositoryException e) {
			// TODO: Add error number
			throw new RepositoryRuntimeException(e);
		}
	}

	private TypedContainerService getService(Type containerType) {
		for (TypedContainerService service : typedContainerServices) {
			if ( service.supports(containerType) ) return service;
		}
		return null;
	}
}
