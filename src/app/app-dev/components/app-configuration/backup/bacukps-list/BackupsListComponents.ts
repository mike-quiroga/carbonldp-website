import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES, } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "backups-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class BackupsListComponent {

	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
