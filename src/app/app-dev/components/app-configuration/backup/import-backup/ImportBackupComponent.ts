import { Component, ElementRef, Input } from "@angular/core";
import { CORE_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Response from "carbonldp/HTTP/Response";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as Pointer from "carbonldp/Pointer";

import BackupsService from "./../BackupsService";
import JobsService from "./../../job/JobsService";
import * as Job from "./../../job/Job"

import template from "./template.html!";
import "./style.css!";

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
	uri:AbstractControl;
	backup:AbstractControl;
	backupFile:AbstractControl;
	backupFileBlob:Blob;

	backups:PersistedDocument.Class[] = [];
	backupsService:BackupsService;
	jobsService:JobsService;
	@Input() appContext:App.Context;

	running:ImportStatus = new ImportStatus();
	uploading:ImportStatus = new ImportStatus();
	creating:ImportStatus = new ImportStatus();
	executing:ImportStatus = new ImportStatus();

	constructor( element:ElementRef, formBuilder:FormBuilder, backupsService:BackupsService, jobsService:JobsService ) {
		this.element = element;
		this.formBuilder = formBuilder;
		this.backupsService = backupsService;
		this.jobsService = jobsService;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$backups = this.$element.find( "select.backups" );
		this.$importForm = this.$element.find( "form.importForm" );
		this.importForm = this.formBuilder.group( {
			uri: [ "", Validators.compose( [ this.uriValidator ] ) ],
			backup: [ "", Validators.compose( [ this.existingBackupValidator.bind( this ) ] ) ],
			backupFile: [ "", Validators.compose( [ this.backupFileValidator.bind( this ) ] ) ],
		}, { validator: Validators.compose( [ this.importFormValidator.bind( this ) ] ) } );
		this.uri = this.importForm.controls[ "uri" ];
		this.backup = this.importForm.controls[ "backup" ];
		this.backupFile = this.importForm.controls[ "backupFile" ];
		this.getBackups();
	}

	getBackups():void {
		this.backupsService.getAll( this.appContext ).then( ( [backups, response]:[PersistedDocument.Class[], Response.Class] )=> {
			this.backups = backups.sort( ( a:any, b:any ) => b.modified < a.modified ? - 1 : b.modified > a.modified ? 1 : 0 );
		} )
	}

	onImportBackup():void {
		this.running.start();
		if ( this.uri.valid )this.createBackupImport( this.uri.value );
		if ( this.backup.valid )this.createBackupImport( this.backup.value );
		if ( this.backupFile.valid )this.uploadBackup( this.backupFileBlob );
	}

	executeImport( importJob:PersistedDocument.Class ):Promise<PersistedDocument.Class> {
		return this.jobsService.runJob( importJob );
	}

	monitorExecution( importJobExecution:PersistedDocument.Class ):void {
		let interval:any = setInterval(
			()=> {
				this.jobsService.checkJobExecution( importJobExecution ).then( ( execution )=> {
						if ( execution[ Job.Execution.STATUS ] !== Job.ExecutionStatus.RUNNING && execution[ Job.Execution.STATUS ] !== Job.ExecutionStatus.QUEUED ) {
							this.executing.finish();
						}
						if ( this.executing.done ) clearInterval( interval );
					}
				);
			}, 3000 );
	}

	onFileChange( event ):void {
		var files:FileList = event.srcElement.files;
		this.backupFileBlob = files[ 0 ];
		(<Control>this.backupFile).updateValueAndValidity();
	}

	onInputLostFocus( event:FocusEvent ):void {
		switch ( event.srcElement.attributes.getNamedItem( "ngcontrol" ).value ) {
			case "uri":
				if ( this.uri.valid ) {
					this.$element.find( "[ngControl='backup']" ).prop( "disabled", true );
					this.$element.find( "[ngControl='backupFile']" ).prop( "disabled", true );
				} else { this.enableAllInputs() }
				break;
			case "backup":
				if ( this.backup.valid ) {
					this.$element.find( "[ngControl='uri']" ).prop( "disabled", true );
					this.$element.find( "[ngControl='backupFile']" ).prop( "disabled", true );
				} else { this.enableAllInputs() }
				break;
			case "backupFile":
				if ( ! ! this.backupFileBlob ) {
					this.$element.find( "[ngControl='uri']" ).prop( "disabled", true );
					this.$element.find( "[ngControl='backup']" ).prop( "disabled", true );
				} else { this.enableAllInputs() }
				break;
		}
	}

	enableAllInputs():void {
		this.$element.find( "[ngControl='uri']" ).prop( "disabled", false );
		this.$element.find( "[ngControl='backup']" ).prop( "disabled", false );
		this.$element.find( "[ngControl='backupFile']" ).prop( "disabled", false );
	}

	uriValidator( uri:AbstractControl ):any {
		if ( uri.value.match( /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/ ) ) {
			return null;
		}
		if ( uri.touched && ! ! uri.value ) {
			return { "invalidURIAddress": true };
		}
		return { "emptyURIAddress": true };
	}

	existingBackupValidator( existingBackup:AbstractControl ):any {
		if ( ! ! existingBackup.value ) {
			return null;
		}
		return { "invalidExistingBackupAddress": true };
	}

	backupFileValidator( backupFile:AbstractControl ):any {
		if ( ! ! this.backupFileBlob ) {
			return null;
		}
		return { "invalidBackupFile": true };
	}

	importFormValidator( importForm:ControlGroup ):any {
		let validForm:boolean = false;
		for ( let control in importForm.controls ) {
			if ( ! ! importForm.controls[ control ].valid )validForm = true;
		}
		if ( validForm ) {
			return null;
		}
		return { "invalidImportForm": true };
	}

	canDisplayImportButtonLoading():boolean {
		return this.uploading.active ? true : this.creating.active ? true : this.executing.active ? true : false;
	}

	uploadBackup( file:Blob ):void {
		this.uploading.start();
		this.backupsService.upload( file, this.appContext ).then(
			( [pointer, response]:[ Pointer.Class, Response.Class ] )=> {
				this.uploading.finish();
				this.createBackupImport( pointer.id );
			}
		);
	}

	createBackupImport( backupURI:string ):void {
		this.creating.start();
		this.jobsService.createImportBackup( backupURI, this.appContext ).then(
			( importJob:PersistedDocument.Class )=> {
				this.creating.finish();
				this.executing.start();
				this.executeImport( importJob ).then(
					( importJobExecution:PersistedDocument.Class )=> {
						this.monitorExecution( importJobExecution );
					}
				);
			}
		).catch( error=>console.error( error ) );
	}

	finishImport():void {
		this.uploading = new ImportStatus();
		this.creating = new ImportStatus();
		this.executing = new ImportStatus();
		this.running = new ImportStatus();
		this.getBackups();
	}
}

class ImportStatus {
	private _active:boolean;
	private _done:boolean;

	get active():boolean { return this._active; }

	get done():boolean { return this._done; }

	set active( value:boolean ) {
		this._active = value;
		this._done = ! value;
	}

	set done( value:boolean ) {
		this._done = value;
		this._active = ! value;
	}

	start():void {
		this.active = true;
	}

	finish():void {
		this.done = true;
	}
}
