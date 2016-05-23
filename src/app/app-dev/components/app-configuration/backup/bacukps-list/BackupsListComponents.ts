import { Component, ElementRef, Input, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES, } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";
import * as Request from "carbonldp/HTTP/Request";
import * as Header from "carbonldp/HTTP/Header";
import * as Response from "carbonldp/HTTP/Response";
import * as NS from "carbonldp/NS";
import * as SDKContext from "carbonldp/SDKContext";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import JobsService from "./../../job/JobsService";

import template from "./template.html!";

@Component( {
	selector: "backups-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class BackupsListComponent {

	element:ElementRef;
	$element:JQuery;

	jobsService:JobsService;
	carbon:Carbon;
	backups:PersistedDocument.Class[];

	@Input() backupJob:PersistedDocument.Class;
	@Input() appContext:App.Context;

	constructor( carbon:Carbon, element:ElementRef, jobsService:JobsService ) {
		this.carbon = carbon;
		this.element = element;
		this.jobsService = jobsService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "backupJob" ] && ! ! changes[ "backupJob" ].currentValue && changes[ "backupJob" ].currentValue !== changes[ "backupJob" ].previousValue ) {
			this.getBackups().then(
				( [backups, response]:[PersistedDocument.Class[],Response.Class] ) => {
					this.backups = backups.map(
						( backup:any )=> {
							backup[ "fileIdentifier" ] = backup[ "https://carbonldp.com/ns/v1/platform#fileIdentifier" ];
							backup[ "result" ] = backup[ "https://carbonldp.com/ns/v1/platform#result" ];
							backup[ "job" ] = backup[ "https://carbonldp.com/ns/v1/platform#job" ];
							backup[ "status" ] = backup[ "https://carbonldp.com/ns/v1/platform#status" ];
							return backup;
						}
					).sort( ( a:any, b:any ) => a.modified < b.modified ? - 1 : a.modified > b.modified ? 1 : 0 );
				}
			);
		}
	}

	getBackups():Promise<[PersistedDocument.Class[],Response.Class]> {
		return this.carbon.documents.getChildren( this.appContext.app.id + "backups/" );
	}

	downloadBackup( uri:string ):void {
		// let requestOptions:HTTP.Request.Options = { sendCredentialsOnCORS: true, };
		// if ( this.appContext && this.appContext.auth.isAuthenticated() ) this.appContext.auth.addAuthentication( requestOptions );
		// HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.NonRDFSource, requestOptions );
		// // HTTP.Request.Util.setContentTypeHeader( "application/octet-stream", requestOptions );
		// requestOptions.headers.set( "Content-Description", new Header.Class( "File Transfer" ) );
		// requestOptions.headers.set( "Content-Disposition", new Header.Class( 'attachment; filename="MyFile.zip"' ) );
		// requestOptions.headers.set( "Content-Transfer", new Header.Class( "encoding: binary" ) );
		// requestOptions.headers.set( "Content-Type", new Header.Class( "binary/octet-stream" ) );
		// // requestOptions.headers.set( "Content-Disposition", new Header.Class( 'attachment; filename="MyFile.zip"' ) );
		//
		// HTTP.Request.Service.get( uri, requestOptions ).then(
		// 	( response:Response.Class ) => {
		// 		console.log( response );
		// 		let file:Blob = new Blob( [ response.data ], { type: "application/octet-stream" } );
		// 		console.log( file );
		// 		let uri:string = window.URL.createObjectURL( file );
		// 		window.open( uri );
		// 		return response;
		// 	} ).catch( ( error ) => {
		// 	console.error( error );
		// 	return Promise.reject( error );
		// } );
	}
}
