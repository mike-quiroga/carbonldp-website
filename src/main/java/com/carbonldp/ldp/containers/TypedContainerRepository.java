package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Map;
import java.util.Set;

public interface TypedContainerRepository {
	public boolean supports( Type containerType );

	public boolean hasMember( URI containerURI, URI possibleMemberURI );

	public boolean hasMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings );

	public Set<Statement> getMembershipTriples( URI containerURI );

	public Set<Statement> getProperties( URI containerURI );

	public Set<URI> findMembers( URI containerURI, String sparqlSelector, Map<String, Value> bindings );

	public Set<URI> filterMembers( URI containerURI, Set<URI> possibleMemberURIs );

	public void addMember( URI containerURI, URI member );

	public void removeMembers( URI containerURI );

	public URI getMembershipResource( URI containerURI );
}
