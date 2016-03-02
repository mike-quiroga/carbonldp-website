package com.carbonldp.jobs;

import com.carbonldp.jobs.TriggerDescription.Type;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface TypedTriggerRepository {
	public boolean supports( Type triggerType );

	public void execute( URI triggerURI );
}
