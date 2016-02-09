import { Component, ElementRef } from 'angular2/core';

import LoginComponent from "./login/LoginComponent";

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';

@Component( {
	selector: 'login-page',
	template: template,
	directives: [ LoginComponent ]
} )
export default class LoginView {
	static parameters = [ [ ElementRef ] ];

	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
	}

}