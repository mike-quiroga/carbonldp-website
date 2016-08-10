import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";

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
	element: ElementRef;
	$element: JQuery;

	constructor( element: ElementRef, title: Title ) {
		this.element = element;
		title.setTitle( "LDP Concepts" );
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
	}

}
