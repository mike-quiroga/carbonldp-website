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
	@Input() backupJob:PersistedDocument.Class;

	constructor( carbon:Carbon, element:ElementRef ) {
		this.carbon = carbon;
		this.element = element;
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
