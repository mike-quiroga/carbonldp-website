package com.carbonldp.ldp.nonrdf.backup.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.web.AbstractController;
import com.carbonldp.web.config.InteractionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
@Controller
@RequestMapping( value = "/platform/apps/*/backups/*/" )
public class BackupController extends AbstractController {
	private BackupGETHandler backupGETHandler;

	@InteractionModel( value = {APIPreferences.InteractionModel.NON_RDF_SOURCE}, handlesDefault = true )
	@RequestMapping( method = {RequestMethod.GET, RequestMethod.HEAD} )
	public ResponseEntity<Object> handleGET( HttpServletRequest request, HttpServletResponse response ) {
		return backupGETHandler.handleRequest( request, response );
	}

	@Autowired
	public void setBackupGETHandler( BackupGETHandler backupGETHandler ) {this.backupGETHandler = backupGETHandler;}
}
