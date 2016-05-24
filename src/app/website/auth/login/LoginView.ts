import { Component, ElementRef } from "@angular/core";

import LoginComponent from "app/components/login/LoginComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "login-page",
	template: template,
	directives: [ LoginComponent, ],
} )
export default class LoginView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
	}

}
