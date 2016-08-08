import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "signup-thanks",
	template: template,
	directives: [ CORE_DIRECTIVES ]
} )

export default class SignupThanksView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		ga( "send", "event", "Newsletter", "Subscription" );
	}
	
}
