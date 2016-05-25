import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Response from "carbonldp/HTTP/Response";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import BackupsService from "./../BackupsService";
import JobsService from "./../../job/JobsService";
import * as Job from "./../../job/Job"

import template from "./template.html!";
// import "./style.css!";

@Component( {
	selector: "import-backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class ImportBackupComponent {

	element:ElementRef;
	$element:JQuery;
	$importForm:JQuery;
	$backups:JQuery;

	formBuilder:FormBuilder;
	importForm:ControlGroup;
	uriGroup:ControlGroup;
	fileGroup:ControlGroup;
	uri:AbstractControl;
	backup:AbstractControl;
	fileBackup:AbstractControl;

	backups:PersistedDocument.Class[] = [];
	backupsService:BackupsService;
	jobsService:JobsService;
	@Input() appContext:App.Context;

	runningImport:boolean = false;

	constructor( element:ElementRef, formBuilder:FormBuilder, backupsService:BackupsService, jobsService:JobsService ) {
		this.element = element;
		this.formBuilder = formBuilder;
		this.backupsService = backupsService;
		this.jobsService = jobsService;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$backups = this.$element.find( "select.backups.list" );
		this.$importForm = this.$element.find( "form.importForm" );
		this.importForm = this.formBuilder.group( {
			uriGroup: this.formBuilder.group( {
				uri: [ "", Validators.compose( [ Validators.required ] ) ],
				backup: [ "" ],
			}, { validator: Validators.compose( [ this.uriGroupValidator ] ) } ),
			fileGroup: this.formBuilder.group( {
				fileBackup: [ "" ],
			}, { validator: Validators.compose( [] ) } ),
		} );
		this.uriGroup = <ControlGroup>this.importForm.controls[ "uriGroup" ];
		this.fileGroup = <ControlGroup>this.importForm.controls[ "fileGroup" ];
		this.uri = this.uriGroup.controls[ "uri" ];
		this.backup = this.uriGroup.controls[ "backup" ];
		this.fileBackup = this.importForm.controls[ "fileBackup" ];
		// console.log( this.importForm );
		this.getBackups();
	}

	ngAfterViewInit():void {
		this.initializeDropdowns();
	}

	initializeDropdowns():void {
		this.$backups.dropdown( {
			onChange: this.onSelectBackup.bind( this ),
		} );
	}

	onSelectBackup( value:string, text:string, option:JQuery ):void {
		(<Control> this.uri).updateValue( value );
		this.uri.updateValueAndValidity();
	}

	getBackups():void {
		this.backupsService.getAll( this.appContext ).then( ( [backups, response]:[PersistedDocument.Class[], Response.Class] )=> {
			this.backups = backups.sort( ( a:any, b:any ) => b.modified < a.modified ? - 1 : b.modified > a.modified ? 1 : 0 );
		} )
	}

	onImportBackup( backupURI:string ):void {
		this.runningImport = true;
		this.jobsService.createImportBackup( backupURI, this.appContext ).then(
			( importJob:PersistedDocument.Class )=> {
				this.runningImport = false;
				console.log( "ImportBackupComponent -> onImportBackup(arguments): %o", importJob );
				this.executeImport( importJob ).then(
					( importJobExecution:PersistedDocument.Class )=> {
						this.runningImport = false;
						// TODO: Check monitor when resolved issue with app block from platform and apps context when importing a backup
						setInterval(
							this.monitorExecution( importJobExecution )
							, 3000
						);
					}
				);
			}
		).catch( error=>console.error( error ) );
	}

	executeImport( importJob:PersistedDocument.Class ):Promise<PersistedDocument.Class> {
		this.runningImport = true;
		return this.jobsService.runJob( importJob );
	}

	monitorExecution( importJobExecution:PersistedDocument.Class ):void {
		this.jobsService.checkJobExecution( importJobExecution ).then(
			( execution:PersistedDocument.Class )=> {
				console.log( "Monitoring Execution: %o", execution );
				this.runningImport = false;
			} );
	}

	uriGroupValidator( corsGroup:ControlGroup ):any {
		let uri:AbstractControl = corsGroup.controls[ "uri" ];
		let backup:AbstractControl = corsGroup.controls[ "backup" ];
		if ( ! ! uri.value && ! ! uri.value.match( /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/ ) ) {
			return null;
		}
		if ( ! ! uri.value ) {
			return { "invalidURIAddress": true };
		}
	}

}
