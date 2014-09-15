package com.base22.carbon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("-- init()");
		}
	}
}
