package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface ContainerService {
	@PreAuthorize( "hasPermission(#containerURI, 'READ')" )
	public Container get( URI containerURI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences );

	@PreAuthorize( "hasPermission(#containerURI, 'READ')" )
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( URI containerURI );

	@PreAuthorize( "hasPermission(#containerURI, 'READ')" )
	public ContainerDescription.Type getContainerType( URI containerURI );

	@PreAuthorize( "hasPermission(#containerURI, 'CREATE_CHILD')" )
	public DateTime createChild( URI containerURI, BasicContainer basicContainer );

	public void removeMembers( URI targetURI );

	public void deleteContainedResources( URI targetURI );

	public void delete( URI targetURI );
}
