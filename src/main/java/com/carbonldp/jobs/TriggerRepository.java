package com.carbonldp.jobs;

import org.openrdf.model.URI;
import com.carbonldp.jobs.TriggerDescription.Type;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface TriggerRepository {

	public void executeTrigger( URI triggerURI );

	public void executeTrigger( URI triggerURI, Type triggerType );

	public TypedTriggerRepository getTypedRepository( Type triggerType );

}
