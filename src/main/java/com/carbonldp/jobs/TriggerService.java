package com.carbonldp.jobs;

import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface TriggerService {

	// TODO: add autorization check
	public void executeTrigger( URI triggerURI );
}
