package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.models.BasicContainer;
import com.carbonldp.models.Container;
import org.openrdf.model.URI;

import java.util.Set;

public interface ContainerService {
	public Container get( URI targetURI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences );

	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( URI targetURI );

	public void createChild( URI targetURI, BasicContainer requestBasicContainer );
}
