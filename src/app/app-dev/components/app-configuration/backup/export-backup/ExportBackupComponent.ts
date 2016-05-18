import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES, } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "export-backup",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class ExportBackupComponent {

	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
