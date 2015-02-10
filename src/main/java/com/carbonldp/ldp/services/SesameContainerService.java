package com.carbonldp.ldp.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.commons.descriptions.ContainerDescription;
import com.carbonldp.commons.descriptions.ContainerDescription.Type;
import com.carbonldp.commons.utils.RDFNodeUtil;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;

@Transactional
public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {

	private final List<TypedContainerService> typedContainerServices;

	public SesameContainerService(SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
			RDFDocumentRepository documentRepository, List<TypedContainerService> typedContainerServices) {
		super(connectionFactory, resourceRepository, documentRepository);
		this.typedContainerServices = typedContainerServices;
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

	private TypedContainerService getService(Type containerType) {
		for (TypedContainerService service : typedContainerServices) {
			if ( service.supports(containerType) ) return service;
		}
		return null;
	}

}
