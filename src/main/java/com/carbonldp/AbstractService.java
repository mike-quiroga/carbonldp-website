package com.carbonldp;

import org.openrdf.spring.SesameConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.carbonldp.commons.Consts;

public abstract class AbstractService {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected final Marker FATAL = MarkerFactory.getMarker(Consts.FATAL);

	@Autowired
	@Qualifier("connectionFactory")
	protected SesameConnectionFactory connectionFactory;
}
