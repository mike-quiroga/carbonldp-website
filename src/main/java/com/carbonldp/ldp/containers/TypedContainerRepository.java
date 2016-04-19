package com.carbonldp.ldp.containers;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import java.util.Map;
import java.util.Set;

public interface TypedContainerRepository {
	public boolean supports( Type containerType );

	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI );

	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings );

	public Set<Statement> getMembershipTriples( IRI containerIRI );

	public Set<Statement> getProperties( IRI containerIRI );

	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings );

	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs );

	public void addMember( IRI containerIRI, IRI member );

	public void removeMember( IRI containerIRI, IRI member );

	public void removeMembers( IRI containerIRI );

	public IRI getMembershipResource( IRI containerIRI );

	public IRI getHasMemberRelation( IRI containerIRI );
}
