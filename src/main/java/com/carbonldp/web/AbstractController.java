package com.carbonldp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class AbstractController {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected final Marker FATAL = MarkerFactory.getMarker( "FATAL" );
}
