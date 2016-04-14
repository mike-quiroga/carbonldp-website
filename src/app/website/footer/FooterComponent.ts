import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Angulartics2On} from "angulartics2/src/core/angulartics2On";
import { Angulartics2 } from "angulartics2";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "footer",
	template: template,
	directives: [ Angulartics2On ]
} )
export default class FooterComponent {
	element:ElementRef;
	$element;
	date = new Date();
	year = this.date.getFullYear();


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