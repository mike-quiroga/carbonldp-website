package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class InvalidSubjectException extends InvalidResourceException {

	public InvalidSubjectException( String subject ) {
		super( new Infraction( 0x200F, "subject", subject ) );
	}
}
