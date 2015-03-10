package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.models.Container;
import com.carbonldp.models.RDFSource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Map;
import java.util.Set;

public interface ContainerRepository {
	public boolean isMember(URI containerURI, URI possibleMemberURI);

	public boolean isMember(URI containerURI, URI possibleMemberURI, Type containerType);

	public Container get(URI containerURI, Set<ContainerRetrievalPreference> preferences);

	public Type getContainerType(URI containerURI);

	public Set<Statement> getProperties(URI containerURI);

	public Set<Statement> getProperties(URI containerURI, Type containerType);

	public Set<Statement> getContainmentTriples(URI containerURI);

	public Set<Statement> getMembershipTriples(URI containerURI);

	public Set<Statement> getMembershipTriples(URI containerURI, Type containerType);

	public Set<ContainerRetrievalPreference> getRetrievalPreferences(URI containerURI);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings);

	public Set<URI> findMembers(URI containerURI, String sparqlSelector, Map<String, Value> bindings, Type containerType);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs);

	public Set<URI> filterMembers(URI containerURI, Set<URI> possibleMemberURIs, Type containerType);

	public void createChild(URI containerURI, RDFSource child);

	public void createChild(URI containerURI, RDFSource child, Type containerType);

	public void addMember(URI containerURI, RDFSource member);

}
