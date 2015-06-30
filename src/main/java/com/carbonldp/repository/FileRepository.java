package com.carbonldp.repository;

import java.io.File;
import java.util.UUID;

public interface FileRepository {
	public boolean exists( UUID uuid );

	public File get( UUID uuid );

	public UUID save( File file );
}
