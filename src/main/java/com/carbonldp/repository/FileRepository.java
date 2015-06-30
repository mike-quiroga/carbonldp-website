package com.carbonldp.repository;

import org.openrdf.model.URI;

import java.io.InputStream;
import java.util.UUID;

public interface FileRepository {

	boolean exists( URI slug );

	InputStream getFileAsInputStream( UUID uuid );

}
