import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "about-carbon-ldp",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	providers: [ Title ]
} )
export default class AboutCarbonLDPViews {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		title.setTitle( "About Carbon LDP" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
