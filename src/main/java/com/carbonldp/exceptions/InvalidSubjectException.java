package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public class InvalidSubjectException extends InvalidResourceException {

	public InvalidSubjectException( String subject ) {
		super( new Infraction( 0x200F, "subject", subject ) );
	}
}
