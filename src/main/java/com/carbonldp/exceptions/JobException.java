package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
public class JobException extends CarbonNoStackTraceRuntimeException {

	public JobException( Infraction infraction ) {
		super( infraction );
	}
}
