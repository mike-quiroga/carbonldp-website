package com.carbonldp.ldp.services;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

import com.carbonldp.commons.descriptions.ContainerDescription.Type;

public interface ContainerService {
	public Type getContainerType(URI containerURI);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, Type containerType);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, Type containerType);

}
