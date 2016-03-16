import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "footer",
	template: template,
	directives: []
} )
export default class FooterComponent {
	element:ElementRef;

	$element;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );

		this.addSocialButtonsAnimations();
	}

	ngOnDestroy():void {
		this.$element.find( ".social-icons .icon" ).unbind( "mouseenter", triggerPulseTransition );
	}

	addSocialButtonsAnimations():void {
		this.$element.find( ".social-icons .icon" ).mouseenter( triggerPulseTransition );
	}
}

function triggerPulseTransition():void {
	var $element = $( this );
	$element.transition( "pulse" );
}