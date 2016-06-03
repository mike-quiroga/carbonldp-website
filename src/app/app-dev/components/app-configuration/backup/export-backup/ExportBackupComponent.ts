import { Component, ElementRef, Input } from "@angular/core";
import { CORE_DIRECTIVES, } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import { Error as HTTPError } from "carbonldp/HTTP/Errors";

import { Message } from "app/app-dev/components/errors-area/ErrorsAreaComponent";
import ErrorMessageComponent from "app/app-dev/components/errors-area/error-message/ErrorMessageComponent";
import JobsService from "./../../job/JobsService";
import * as Job from "./../../job/Job";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "export-backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ErrorMessageComponent ],
} )

export default class ExportBackupComponent {
	carbon:Carbon;
	element:ElementRef;
	$element:JQuery;

	executingBackup:boolean = false;

	@Input() appContext:App.Context;
	@Input() backupJob:PersistedDocument.Class;
	errorMessages:Message[] = [];
	jobsService:JobsService;
	exportSuccess:boolean;

	constructor( carbon:Carbon, element:ElementRef, jobsService:JobsService ) {
		this.carbon = carbon;
		this.element = element;
		this.jobsService = jobsService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		// TODO: Add backup scheduler with datetime when Platform supports it.
	}

	onGenerateBackup():void {
		this.executingBackup = true;

		this.jobsService.runJob( this.backupJob ).then( ( execution:PersistedDocument.Class )=> {
			return this.monitorExecution( execution ).catch( ( executionOrError:HTTPError|PersistedDocument.Class ) => {
				// TODO: If its an HTTPError, return Promise.reject( executionOrError );

			});
		} ).then( ()=> {
			this.exportSuccess = true;
		} ).catch( ( error:HTTPError )=> {
			// TODO: This catch block is catching errors that are thrown by monitorExecution. This means execution objects can also come instead of an HTTPError
			let errorMessage:Message = <Message>{
				title: error.name,
				content: "Couldn't execute backup.",
				endpoint: (<any>error.response.request).responseURL,
				statusCode: "" + (<XMLHttpRequest>error.response.request).status,
				statusMessage: (<XMLHttpRequest>error.response.request).statusText
			};
			this.errorMessages.push( errorMessage );
		} ).then( ()=> {
			this.executingBackup = false;
		} );
	}

	monitorExecution( execution:PersistedDocument.Class ):Promise<PersistedDocument.Class> {
		return new Promise<PersistedDocument.Class>( ( resolve:( result:any ) => void, reject:( error:HTTPError|PersistedDocument.Class ) => void ) => {
			let interval:number = setInterval( ()=> {
				execution.refresh().then( ()=> {
					if ( execution[ Job.Execution.STATUS ] !== Job.ExecutionStatus.FINISHED ) {
						clearInterval( interval );
						resolve( execution );
					} else if ( execution[ Job.Execution.STATUS ] !== Job.ExecutionStatus.ERROR ) {
						clearInterval( interval );
						reject( execution );
					}
				} ).catch( () => {
					// TODO: What happens if the refresh method fails?
				} );
			}, 3000 );
		} );
	}

	removeMessage( index:number ):void {
		this.errorMessages.splice( index, 1 );
	}

	onCloseSuccess():void {
		this.exportSuccess = false;
	}
}
