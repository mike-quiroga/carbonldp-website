package com.carbonldp.repository;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import java.io.InputStream;
import java.util.UUID;

public class LocalFileRepository implements FileRepository {
	SesameConnectionFactory connectionFactory;

	public LocalFileRepository( SesameConnectionFactory connectionFactory ) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public boolean exists( URI slug ) {

		//TODO Implement
		return false;

	}

	@Override
	public InputStream getFileAsInputStream( UUID Uuuid ) {
		//TODO implement
		//TODO get AppContext here

		return null;
	}

}
