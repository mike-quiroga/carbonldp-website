import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./about-carbon-LDP.view.html!";

@Component( {
	selector: "about-carbon-ldp",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )
export class AboutCarbonLDPView implements AfterViewInit{
	private element:ElementRef;
	private $element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
export default AboutCarbonLDPView;