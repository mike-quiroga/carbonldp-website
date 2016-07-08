import { Component, ElementRef } from "@angular/core";

import { NotAuthenticated } from "angular2-carbonldp/decorators";

import LoginComponent from "app/components/login/LoginComponent";
import FooterComponent from "app/app-dev/footer/FooterComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@NotAuthenticated( {
	redirectTo: [ "/AppDev" ],
} )
@Component( {
	selector: "app-dev-login-page.big-stone1",
	template: template,
	directives: [ LoginComponent, FooterComponent, ],
} )
export default class AppDevLoginView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
	}

}