import { Component, Input } from "@angular/core";

import "semantic-ui/semantic";

import * as App from "carbonldp/App";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import JobsService from "./../job/JobsService";
import * as Job from "./../job/Job";

import ImportBackupComponent from "./import-backup/ImportBackupComponent"
import ExportBackupComponent from "./export-backup/ExportBackupComponent"
import BackupsListComponent from "./bacukps-list/BackupsListComponents"

import template from "./template.html!";

@Component( {
	selector: "backup",
	template: template,
	directives: [ ExportBackupComponent, ImportBackupComponent, BackupsListComponent ],
} )

export default class BackupComponent {

	backupJob:PersistedDocument.Class;
	jobsService:JobsService;
	@Input() appContext:App.Context;

	constructor( jobsService:JobsService ) {
		this.jobsService = jobsService;
	}

	ngOnInit():void {
		this.jobsService.getJobOfType( Job.Type.EXPORT_BACKUP, this.appContext ).then( ( job:PersistedDocument.Class )=> {
			if ( ! ! job ) this.backupJob = job;
			else this.jobsService.createExportBackup( this.appContext ).then( ( exportBackupJob:PersistedDocument.Class ) => {
				this.backupJob = exportBackupJob;
			} );
		} );
	}

}
