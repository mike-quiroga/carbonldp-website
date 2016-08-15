import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./signup-thanks.view.html!";
import style from "./signup-thanks.view.css!text";

@Component( {
	selector: "signup-thanks",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	styles: [ style ],
} )

export class SignupThanksView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		ga( "send", "event", "Newsletter", "Subscription" );
	}

}

export default SignupThanksView;
