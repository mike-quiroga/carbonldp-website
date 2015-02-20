package com.carbonldp.ldp.services;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.models.RDFSource;

public interface ContainerService {
	public boolean isMember(URI containerURI, URI possibleMemberURI);

	public boolean isMember(URI containerURI, URI possibleMemberURI, Type containerType);

	public Type getContainerType(URI containerURI);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, Type containerType);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, Type containerType);

	public void createChild(URI containerURI, RDFSource child);

	public void createChild(URI containerURI, RDFSource child, Type containerType);

	public void addMember(URI containerURI, RDFSource member);

}
