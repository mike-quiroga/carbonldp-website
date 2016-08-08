package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.http.OrderByRetrievalPreferences;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.joda.time.DateTime;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;
import java.util.Set;

public interface ContainerService {
	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	public Container get( IRI containerIRI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences, OrderByRetrievalPreferences orderByRetrievalPreferences );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	public ContainerDescription.Type getContainerType( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'CREATE_CHILD')" )
	public DateTime createChild( IRI containerIRI, BasicContainer basicContainer );

	@PreAuthorize( "hasPermission(#containerIRI, 'ADD_MEMBER')" )
	public void addMembers( IRI containerIRI, Set<IRI> members );

	@PreAuthorize( "hasPermission(#containerIRI, 'ADD_MEMBER')" )
	public void addMember( IRI containerIRI, IRI member );

	@PreAuthorize( "hasPermission(#containerIRI, 'REMOVE_MEMBER')" )
	public void removeMembers( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'REMOVE_MEMBER')" )
	public void removeMembers( IRI containerIRI, Set<IRI> members );

	@PreAuthorize( "hasPermission(#containerIRI, 'REMOVE_MEMBER')" )
	public void removeMember( IRI containerIRI, IRI member );

	public void deleteContainedResources( IRI targetIRI );

	@PreAuthorize( "hasPermission(#targetIRI, 'DELETE')" )
	public void delete( IRI targetIRI );

	@PreAuthorize( "hasPermission(#targetIRI, 'UPLOAD')" )
	public void createNonRDFResource( IRI targetIRI, IRI resourceIRI, File resourceFile, String mimeType );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	public Set<Statement> getMembershipTriples( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	@PostFilter( "hasPermission(filterObject.getObject(), 'READ')" )
	public Set<Statement> getReadableMembershipResourcesTriples( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	@PostFilter( "hasPermission(filterObject.getObject(), 'READ')" )
	public Set<Statement> getReadableContainedResourcesTriples( IRI containerIRI );

	@PreAuthorize( "hasPermission(#containerIRI, 'READ')" )
	@PostFilter( "!hasPermission(filterObject.getObject(), 'READ')" )
	public Set<Statement> getNonReadableMembershipResourcesTriples( IRI containerIRI );
}
