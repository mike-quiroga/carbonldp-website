package com.carbonldp.platform.api;

import com.carbonldp.AbstractComponent;
import com.carbonldp.authorization.Platform;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.rdf.RDFResourceDescription;
import org.joda.time.DateTime;

import java.util.Properties;

public class PlatformAPIRepository extends AbstractComponent {

	private final String version;
	private final DateTime buildDate;

	public PlatformAPIRepository( Properties properties ) {
		version = properties.getProperty( "project.version" );
		buildDate = DateTime.parse( properties.getProperty( "project.build-date" ) );
	}

	public PlatformAPI get() {
		PlatformAPI api = PlatformAPIFactory.create( new RDFResource( Platform.API_URI ) );
		api.addType( RDFResourceDescription.Resource.VOLATILE.getURI() );
		api.setVersion( this.version );
		api.setBuildDate( this.buildDate );
		return api;
	}
}
