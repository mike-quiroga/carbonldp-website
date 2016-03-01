package com.carbonldp.ldp.nonrdf.backup.web;

import com.carbonldp.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Controller
@RequestMapping( value = "/apps/*/backups/*/" )
public class BackupController extends AbstractController {
	private BackupGETHandler backupGETHandler;

	//TODO: Complete this controller

	@Autowired
	public void setBackupGETHandler( BackupGETHandler backupGETHandler ) {this.backupGETHandler = backupGETHandler;}
}
