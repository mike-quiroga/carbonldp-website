package com.carbonldp.ldp.services;

import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.models.RDFSource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Map;
import java.util.Set;

public interface TypedContainerService {
	public boolean supports(Type containerType);

	public boolean isMember(URI containerURI, URI possibleMemberURI);

	public Set<Statement> getMembershipTriples(URI containerURI);

	public Set<Statement> getProperties(URI containerURI);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs);

	public RDFSource addMember(URI containerURI, RDFSource child);
}
