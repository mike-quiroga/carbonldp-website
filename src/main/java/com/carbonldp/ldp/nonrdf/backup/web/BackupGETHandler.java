package com.carbonldp.ldp.nonrdf.backup.web;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.Vars;
import com.carbonldp.ldp.web.AbstractGETRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;

import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
@RequestHandler
public class BackupGETHandler extends AbstractGETRequestHandler {

	@Override
	protected void addNonRDFAllowHeader( IRI targetIRI, HttpServletResponse response ) {

		response.addHeader( HTTPHeaders.ALLOW, "GET, PUT, DELETE, OPTIONS" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "*/*" );

		response.addHeader( HTTPHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + Vars.getInstance().getBackUpDownloadFileName() + "\"" );
		response.addHeader( HTTPHeaders.CONTENT_TYPE, Consts.ZIP );
		response.addHeader( HTTPHeaders.CONTENT_DESCRIPTION, "File Transfer" );
		response.addHeader( HTTPHeaders.CONTENT_TRANSFER_ENCODING, "binary" );
	}
}
