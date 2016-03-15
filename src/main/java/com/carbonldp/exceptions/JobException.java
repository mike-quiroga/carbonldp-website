package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobException extends CarbonNoStackTraceRuntimeException {

	private List<Infraction> infractions;

	public JobException( List<Infraction> infractions ) {
		this.infractions = infractions;
	}

	public JobException( Infraction infraction ) {
		infractions = new ArrayList<>();
		infractions.add( infraction );
	}

	public List<Infraction> getInfractions() {
		return infractions;
	}
}
