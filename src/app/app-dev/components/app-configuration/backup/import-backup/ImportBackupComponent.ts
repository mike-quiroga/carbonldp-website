import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Response from "carbonldp/HTTP/Response";
import * as PersistedDocument from "carbonldp/PersistedDocument";

import BackupsService from "./../BackupsService";

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
	$backups:Jquery;

	formBuilder:FormBuilder;
	importForm:ControlGroup;
	uriGroup:ControlGroup;
	fileGroup:ControlGroup;
	uri:AbstractControl;
	backup:AbstractControl;
	fileBackup:AbstractControl;

	backups:PersistedDocument.Class[];
	backupsService:BackupsService;
	@Input() appContext:App.Context;

	constructor( element:ElementRef, formBuilder:FormBuilder, backupsService:BackupsService ) {
		this.element = element;
		this.formBuilder = formBuilder;
		this.backupsService = backupsService;
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
			this.backups = backups;
		} )
	}

	uriGroupValidator( corsGroup:ControlGroup ):any {
		let uri:AbstractControl = corsGroup.controls[ "uri" ];
		let backup:AbstractControl = corsGroup.controls[ "backup" ];
		if ( ! ! uri.value && ! ! uri.value.match( /^http(s?):\/\/((\w+\.)?\w+\.\w+|((2[0-5]{2}|1[0-9]{2}|[0-9]{1,2})\.){3}(2[0-5]{2}|1[0-9]{2}|[0-9]{1,2}))(\/)?$/gm ) ) {
			return null;
		}
		if ( ! ! uri.value ) {
			return { "invalidURIAddress": true };
		}
	}

}
