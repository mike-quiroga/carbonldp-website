import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";
import * as Response from "carbonldp/HTTP/Response";
import * as NS from "carbonldp/NS";
import * as SDKContext from "carbonldp/SDKContext";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import JobsService from "./../job/JobsService"

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
		this.jobsService.getJobOfType( "https://carbonldp.com/ns/v1/platform#ExportBackupJob", this.appContext ).then(
			( job:PersistedDocument.Class )=> {
				if ( ! job ) {
					this.createBackupJob( this.appContext.app.id + "jobs/", this.appContext ).then(
						( response:HTTP.Response.Class ) => {
							if ( response.status !== HTTP.StatusCode.CREATED ) throw new Error( "The job couldn't be created successfully. Response Status: " + response.status );
							this.jobsService.getJobOfType( "https://carbonldp.com/ns/v1/platform#ExportBackupJob", this.appContext ).then(
								( createdJob:PersistedDocument.Class )=> {
									console.log( "The created Jobs is: %o", createdJob );
									this.backupJob = createdJob;
								}
							).catch(
								( error:Error )=> {
									console.error( error );
									Promise.reject( error );
								}
							);
						}
					);
				} else {
					this.backupJob = job;
					console.log( "Job found: %o", job );
				}
			}
		);
	}

	private createBackupJob( uri:string, appContext:SDKContext.Class ):Promise<HTTP.Response.Class> {
		let requestOptions:HTTP.Request.Options = { sendCredentialsOnCORS: true, };
		if ( appContext && appContext.auth.isAuthenticated() ) appContext.auth.addAuthentication( requestOptions );
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.Container, requestOptions );
		HTTP.Request.Util.setContentTypeHeader( "text/turtle", requestOptions );
		let body:string =
			`@prefix c:  <https://carbonldp.com/ns/v1/platform#>.
			<>
			a c:ExportBackupJob.`;

		return HTTP.Request.Service.post( uri, body, requestOptions ).then( ( response:Response.Class ) => {
			return response;
		} ).catch( ( error ) => {
			console.error( error );
			return Promise.reject( error );
		} );
	}


	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( this.appContext );
	}

}
