package com.base22.carbon.api.ldp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class AbstractBaseRdfAPIController {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected final Marker FATAL = MarkerFactory.getMarker("FATAL");
}
