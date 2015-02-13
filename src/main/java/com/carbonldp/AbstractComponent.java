package com.carbonldp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.carbonldp.Consts;

public abstract class AbstractComponent {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected final Marker FATAL = MarkerFactory.getMarker(Consts.FATAL);
}
