import { Component, ElementRef, Input } from "@angular/core";

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
	directives: [ ErrorMessageComponent ],
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
				if ( executionOrError.hasOwnProperty( "response" ) ) return Promise.reject( executionOrError );
				let errorMessage:Message = <Message>{
					title: "Couldn't execute backup.",
					content: "An error occurred while executing your export backup job. This may be caused due to a bad configuration during the creation of your job.",
					statusMessage: execution[ Job.Execution.ERROR_DESCRIPTION ]
				};
				this.errorMessages.push( errorMessage );
			} );
		} ).then( ()=> {
			this.exportSuccess = true;
		} ).catch( ( error:HTTPError )=> {
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
					switch ( execution[ Job.Execution.STATUS ].id ) {
						case Job.ExecutionStatus.FINISHED:
							clearInterval( interval );
							resolve( execution );
							break;
						case Job.ExecutionStatus.ERROR:
							clearInterval( interval );
							reject( execution );
							break;
					}
				} ).catch( ( error:HTTPError ) => {
					let errorMessage:Message = <Message>{
						title: error.name,
						content: "Couldn't monitor the exporting backup status.",
						endpoint: (<any>error.response.request).responseURL,
						statusCode: "" + (<XMLHttpRequest>error.response.request).status,
						statusMessage: (<XMLHttpRequest>error.response.request).statusText
					};
					this.errorMessages = [ errorMessage ];
					clearInterval( interval );
					this.executingBackup = false;
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
