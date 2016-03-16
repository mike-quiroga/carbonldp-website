import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "getting-started",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	providers: [ Title ],
} )
export default class GettingStartedView {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;

		this.title = title;
		this.title.setTitle( "Getting started - JavaScript SDK" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}
}
