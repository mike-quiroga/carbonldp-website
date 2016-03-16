package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobException extends CarbonNoStackTraceRuntimeException {

	public JobException( Infraction infraction ) {
		super( infraction );
	}
}
