package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AbstractNonRDFRequestHandler extends AbstractLDPRequestHandler {

	protected File createTemporaryFile( InputStream inputStream ) {
		File temporaryFile;
		FileOutputStream outputStream;
		try {
			temporaryFile = File.createTempFile( createRandomSlug(), null );
			temporaryFile.deleteOnExit(); // TODO: See if this makes the VM log warnings/errors

			outputStream = new FileOutputStream( temporaryFile );
			try {
				IOUtils.copy( inputStream, outputStream );
			} finally {
				try {
					outputStream.close();
				} catch ( IOException e ) {
					LOG.warn( "The outputStream couldn't be closed. Exception: ", e );
				}
			}
		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The temporary file couldn't be created. Exception:", e );
		} finally {
			try {
				inputStream.close();
			} catch ( IOException e ) {
				LOG.warn( "The inputStream couldn't be closed. Exception: ", e );
			}
		}
		return temporaryFile;
	}

	protected void deleteTemporaryFile( File file ) {
		boolean wasDeleted = false;
		try {
			wasDeleted = file.delete();
		} catch ( SecurityException e ) {
			LOG.warn( "A temporary file couldn't be deleted. Exception:", e );
		}
		if ( ! wasDeleted ) LOG.warn( "The temporary file: '{}', couldn't be deleted.", file.toString() );
	}

	protected String createRandomSlug() {
		Random random = new Random();
		return String.valueOf( Math.abs( random.nextLong() ) );
	}
}
