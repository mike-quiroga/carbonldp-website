import { Component, ElementRef, Input } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import BackupComponent from "./backup/BackupComponent"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "app-configuration",
	template: template,
	directives: [ CORE_DIRECTIVES, BackupComponent ],
} )

export default class AppConfigurationComponent {

	element:ElementRef;
	$element:JQuery;
	@Input() appContext:App.Context;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$element.find( ".config.options.menu .item" ).tab();
	}

}
