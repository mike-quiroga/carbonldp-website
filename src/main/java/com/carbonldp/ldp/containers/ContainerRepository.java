package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.http.OrderByRetrievalPreferences;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.sources.RDFSource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface ContainerRepository {
	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI );

	public boolean hasMember( IRI containerIRI, IRI possibleMemberIRI, Type containerType );

	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings );

	public boolean hasMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, Type containerType );

	public Type getContainerType( IRI containerIRI );

	public Set<Statement> getProperties( IRI containerIRI );

	public Set<Statement> getProperties( IRI containerIRI, Type containerType );

	public Set<IRI> getContainedIRIs( IRI containerIRI );

	public Set<IRI> getContainedIRIs( IRI targetIRI, OrderByRetrievalPreferences orderByRetrievalPreferences );

	public Set<Statement> getContainmentTriples( IRI containerIRI );

	public Set<Statement> getMembershipTriples( IRI containerIRI );

	public Set<Statement> getMembershipTriples( IRI containerIRI, Type containerType );

	public Set<ContainerRetrievalPreference> getRetrievalPreferences( IRI containerIRI );

	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings );

	public Set<IRI> findMembers( IRI containerIRI, String sparqlSelector, Map<String, Value> bindings, Type containerType );

	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs );

	public Set<IRI> filterMembers( IRI containerIRI, Set<IRI> possibleMemberIRIs, Type containerType );

	public void create( Container rootContainer );

	public void createNonRDFResource( IRI targetIRI, IRI resourceIRI, File requestEntity, String contentType );

	public void createChild( IRI containerIRI, RDFSource child );

	public void createChild( IRI containerIRI, RDFSource child, Type containerType );

	public void addMember( IRI containerIRI, IRI member );

	public void removeMember( IRI containerIRI, IRI member );

	public void removeMember( IRI containerIRI, IRI member, Type containerType );

	public void addMember( IRI containerIRI, IRI member, Type containerType );

	public void removeMembers( IRI targetIRI );

	public void removeMembers( IRI targetIRI, Type containerType );

	public TypedContainerRepository getTypedRepository( Type containerType );

	public Set<IRI> getMemberIRIs( IRI targetIRI, OrderByRetrievalPreferences orderByRetrievalPreferences );

	public Set<IRI> getMemberIRIs( IRI targetIRI );
}
