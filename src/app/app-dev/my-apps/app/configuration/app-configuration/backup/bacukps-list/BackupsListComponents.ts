import { Component, ElementRef, Input, SimpleChange } from "@angular/core";
import { CORE_DIRECTIVES, } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Response from "carbonldp/HTTP/Response";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import { StatusCode as HTTPStatusCode } from "carbonldp/HTTP";
import { Error as HTTPError } from "carbonldp/HTTP/Errors";

import BackupsService from "./../BackupsService";
import { Message } from "app/app-dev/components/errors-area/ErrorsAreaComponent";
import ErrorMessageComponent from "app/app-dev/components/errors-area/error-message/ErrorMessageComponent";

import template from "./template.html!";
import style from "./style.css!text";

@Component( {
	selector: "backups-list",
	template: template,
	styles: [ style ],
	directives: [ ErrorMessageComponent ],
} )

export default class BackupsListComponent {

	element:ElementRef;
	$element:JQuery;
	$deleteBackupConfirmationModal:JQuery;

	backupsService:BackupsService;
	carbon:Carbon;
	backups:PersistedDocument.Class[];
	askingBackupToRemove:PersistedDocument.Class;
	loadingBackups:boolean = false;
	deletingBackup:boolean = false;
	errorMessages:Message[] = [];
	refreshPeriod:number = 15000;
	deleteMessages:Message[] = [];

	@Input() backupJob:PersistedDocument.Class;
	@Input() appContext:App.Context;

	constructor( carbon:Carbon, element:ElementRef, backupsService:BackupsService ) {
		this.carbon = carbon;
		this.element = element;
		this.backupsService = backupsService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$deleteBackupConfirmationModal = this.$element.find( ".delete.backup.modal" );
		this.initializeModals();
	}

	initializeModals():void {
		this.$deleteBackupConfirmationModal.modal( {
			closable: false,
			blurring: true,
			onApprove: ()=>false
		} );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if( changes[ "backupJob" ] && ! ! changes[ "backupJob" ].currentValue && changes[ "backupJob" ].currentValue !== changes[ "backupJob" ].previousValue ) {
			this.loadingBackups = true;
			this.getBackups().then( ( backups:PersistedDocument.Class[] ) => {
				this.loadingBackups = false;
			} ).catch( ()=>this.loadingBackups = false );
			this.monitorBackups();
		}
	}

	monitorBackups():void {
		setInterval( ()=> this.getBackups(), this.refreshPeriod );
	}

	getBackups():Promise<PersistedDocument.Class[]> {
		this.errorMessages = [];
		return this.backupsService.getAll( this.appContext ).then(
			( [backups, response]:[PersistedDocument.Class[],Response.Class] ) => {
				backups = backups.sort( ( a:any, b:any ) => b.modified < a.modified ? - 1 : b.modified > a.modified ? 1 : 0 );
				this.backups = backups;
				return backups;
			}
		).catch( ( error:HTTPError ) => {
			let errorMessage:Message = <Message>{
				title: error.name,
				content: `Couldn't fetch backups. I'll try again in ${(this.refreshPeriod / 1000)} seconds.`,
				endpoint: (<any>error.response.request).responseURL,
				statusCode: "" + (<XMLHttpRequest>error.response.request).status,
				statusMessage: (<XMLHttpRequest>error.response.request).statusText
			};
			this.errorMessages.push( errorMessage );
			return error;
		} );
	}

	downloadBackup( uri:string ):void {
		window.open( uri );
		// TODO: implement a way to download without prompt when Platform supports it.
	}

	askToDeleteBackup( askingBackupToRemove:PersistedDocument.Class ):void {
		this.askingBackupToRemove = askingBackupToRemove;
		this.$deleteBackupConfirmationModal.modal( "show" );
	}

	deleteBackup( backup:PersistedDocument.Class ):Promise<Response.Class> {
		this.deletingBackup = true;
		return this.backupsService.delete( backup.id, this.appContext ).then( ( response:Response.Class ):Response.Class => {
			if( response.status !== HTTPStatusCode.OK ) return <any>Promise.reject( response );
			this.getBackups();
			this.closeDeleteModal();
			return response;
		} ).catch( ( errorOrResponse:HTTPError|Response.Class )=> {
			let deleteMessage:Message;
			if( errorOrResponse.hasOwnProperty( "response" ) ) {
				deleteMessage = <Message>{
					title: (<HTTPError>errorOrResponse).name,
					content: "Couldn't delete the backup.",
					endpoint: (<any>(<HTTPError>errorOrResponse).response.request).responseURL,
					statusCode: "" + (<XMLHttpRequest>(<HTTPError>errorOrResponse).response.request).status,
					statusMessage: (<XMLHttpRequest>(<HTTPError>errorOrResponse).response.request).statusText
				};
			} else {
				deleteMessage = <Message>{
					title: (<XMLHttpRequest>(<Response.Class>errorOrResponse).request).statusText,
					content: "Couldn't delete the backup.",
					endpoint: (<any>(<Response.Class>errorOrResponse).request).responseURL,
					statusCode: "" + (<Response.Class>errorOrResponse).status,
					statusMessage: (<XMLHttpRequest>(<Response.Class>errorOrResponse).request).statusText
				};
			}
			this.deleteMessages.push( deleteMessage );
		} ).then( ( response:Response.Class )=> {
			this.deletingBackup = false;
			return response;
		} );
	}

	removeDeleteErrorMessage( index:number ):void {
		this.deleteMessages.slice( index );
	}

	closeDeleteModal():void {
		this.$deleteBackupConfirmationModal.modal( "hide" );
	}
}
