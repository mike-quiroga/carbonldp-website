import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";

import { Angulartics2On } from "angulartics2";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "signup-thanks",
	template: template,
	directives: [ CORE_DIRECTIVES, Angulartics2On ],
	providers: [ Title ]
} )

export default class SignupThanksView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		title.setTitle( "Thank you!" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		//ga( 'send', 'pageview', location.pathname );
	}
}
