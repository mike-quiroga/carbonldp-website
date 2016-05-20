import { Component, ElementRef, Input, SimpleChange } from "angular2/core";
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
			this.getBackups();
		}
	}

	getBackups():Promise<PersistedDocument.Class[]> {
		return this.carbon.documents.getChildren( this.backupJob.id ).then(
			( [backups, response]:[PersistedDocument.Class[],Response.Class] )=> {
				return backups;
			}
		);
	}

}
