package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class InvalidResourceException extends java.lang.IllegalArgumentException {

	private List<Infraction> infractions;

	public InvalidResourceException( List<Infraction> infractions ) {
		this.infractions = infractions;
	}

	public InvalidResourceException( Infraction infraction ) {
		infractions = new ArrayList<>();
		infractions.add( infraction );
	}

	public List<Infraction> getInfractions() {
		return infractions;
	}

}
