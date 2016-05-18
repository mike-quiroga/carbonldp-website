import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import ImportBackupComponent from "./import-backup/ImportBackupComponent"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ImportBackupComponent ],
} )

export default class BackupComponent {

	element:ElementRef;
	$element:JQuery;
	@Input() appContext:App.Context;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( this.appContext );
	}

}
