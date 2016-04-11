import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";


@Component( {
	selector: "ldp-concepts",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	providers: [ Title ],
} )
export default class LDPConceptsView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		title.setTitle( "LDP Concepts" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		//ga( "send", "pageview", location.pathname );
	}
}
