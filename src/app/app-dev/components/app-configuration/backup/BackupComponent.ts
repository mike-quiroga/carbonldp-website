import { Component, ElementRef, Input } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbonldp/App";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import JobsService from "./../job/JobsService";
import * as Job from "./../job/Job";

import ImportBackupComponent from "./import-backup/ImportBackupComponent"
import ExportBackupComponent from "./export-backup/ExportBackupComponent"
import BackupsListComponent from "./bacukps-list/BackupsListComponents"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ExportBackupComponent, ImportBackupComponent, BackupsListComponent ],
} )

export default class BackupComponent {

	element:ElementRef;
	$element:JQuery;
	backupJob:PersistedDocument.Class;
	jobsService:JobsService;
	@Input() appContext:App.Context;

	constructor( element:ElementRef, jobsService:JobsService ) {
		this.element = element;
		this.jobsService = jobsService;
	}

	ngOnInit():void {
		this.jobsService.getJobOfType( Job.Type.EXPORT_BACKUP, this.appContext ).then( ( job:PersistedDocument.Class )=> {
			if ( ! job ) {
				this.jobsService.createExportBackup( this.appContext ).then( ( exportBackupJob:PersistedDocument.Class ) => {
					console.log( "The created Jobs is: %o", exportBackupJob );
					this.backupJob = exportBackupJob;
				} );
			} else {
				this.backupJob = job;
				console.log( "Job found: %o", job );
			}
		} );
	}


	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
