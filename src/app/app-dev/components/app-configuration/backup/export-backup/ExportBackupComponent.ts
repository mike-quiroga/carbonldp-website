import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES, } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";
import * as Response from "carbonldp/HTTP/Response";
import * as NS from "carbonldp/NS";
import * as SDKContext from "carbonldp/SDKContext";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "export-backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class ExportBackupComponent {
	carbon:Carbon;
	element:ElementRef;
	$element:JQuery;

	executingBackup:boolean = false;

	@Input() appContext:App.Context;
	private backupJob:PersistedDocument.Class;

	constructor( carbon:Carbon, element:ElementRef ) {
		this.carbon = carbon;
		this.element = element;
	}

	ngOnInit():void {
		this.findJob( "https://carbonldp.com/ns/v1/platform#ExportBackupJob" ).then(
			( persistedDocument:PersistedDocument.Class )=> {
				if ( ! persistedDocument ) {
					this.createFirstBackupJob().then(
						( createdJob:PersistedDocument.Class )=> {
							console.log( "The created Jobs is: %o", createdJob );
							this.backupJob = createdJob;
						}
					);
				} else {
					this.backupJob = persistedDocument;
					console.log( "Job found: %o", persistedDocument );
				}
			}
		);
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( this.appContext );
	}

	createBackup():void {
		this.executingBackup = true;
		this.requestBackup( this.backupJob.id, this.appContext ).then(
			()=> {
				this.executingBackup = false;
			}
		);
	}

	// TODO: implement job service in a service component
	private getJobs( appContext:SDKContext.Class ):Promise<[PersistedDocument.Class[], HTTP.Response.Class]> {
		let uri:string = (<App.Context>appContext).app.id + "jobs/";
		return this.carbon.documents.getChildren( uri );
	}

	// TODO: add this function to the job service
	findJob( type:string ):Promise<PersistedDocument.Class> {
		return new Promise<PersistedDocument.Class>(
			( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
				this.getJobs( this.appContext ).then(
					( [persistedDocuments, response]:[PersistedDocument.Class[], HTTP.Response.Class] )=> {
						resolve( persistedDocuments.find( ( doc:PersistedDocument.Class ) => doc.types.indexOf( type ) !== - 1 ) );
					},
					( error )=> {
						console.error( error );
						reject( error );
					}
				);
			} );
	}

	private createFirstBackupJob():Promise<PersistedDocument.Class> {
		return this.createBackupJob( this.appContext.app.id + "jobs/", this.appContext ).then(
			( response:HTTP.Response.Class ) => {
				if ( response.status !== HTTP.StatusCode.CREATED ) return null;
				return this.findJob( "https://carbonldp.com/ns/v1/platform#ExportBackupJob" ).then(
					( createdJob:PersistedDocument.Class )=> {
						return createdJob;
					}
				).catch(
					( error:Error )=> {
						console.error( error );
						Promise.reject( error );
					}
				);
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

	private requestBackup( uri:string, appContext:SDKContext.Class ):Promise<Response.Class> {
		let requestOptions:HTTP.Request.Options = { sendCredentialsOnCORS: true, };
		if ( appContext && appContext.auth.isAuthenticated() ) appContext.auth.addAuthentication( requestOptions );
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.Container, requestOptions );
		HTTP.Request.Util.setContentTypeHeader( "text/turtle", requestOptions );
		let body:string =
			`@prefix c:  <https://carbonldp.com/ns/v1/platform#>.
			<>
			a c:Execution.`;

		return HTTP.Request.Service.post( uri, body, requestOptions ).then( ( response:Response.Class ) => {
			console.log( response );
			if ( response.status !== HTTP.StatusCode.OK ) return null;
			return response;
		} ).catch( ( error ) => {
			console.error( error );
			return Promise.reject( error );
		} );
	}
}
