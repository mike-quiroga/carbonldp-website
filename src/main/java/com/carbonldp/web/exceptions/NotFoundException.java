package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractWebRuntimeException {
	private static final long serialVersionUID = 2729058903804280565L;
	private static final HttpStatus defaultStatus = HttpStatus.NOT_FOUND;
	private static final int defaultErrorCode = 0x4001;

	public NotFoundException() {
		super( defaultErrorCode, defaultStatus );
	}

}
