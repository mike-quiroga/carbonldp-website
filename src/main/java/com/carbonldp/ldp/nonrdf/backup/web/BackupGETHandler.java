package com.carbonldp.ldp.nonrdf.backup.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class BackupGETHandler extends AbstractGETRequestHandler {
	@Override
	protected void addNonRDFHeader( URI targetURI, HttpServletResponse response ) {

		//TODO: Add headers to httpHeaders and create constants to the strings
		response.addHeader( HTTPHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Backup.zip\"" );
		response.addHeader( HTTPHeaders.CONTENT_TYPE, "application/zip" );
		response.addHeader( "Content-Description", "File Transfer" );
		response.addHeader( "Content-Transfer-Encoding", "binary" );
	}
}
