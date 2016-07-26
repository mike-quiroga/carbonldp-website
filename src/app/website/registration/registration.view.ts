import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import { RegistrationComponent } from "./registration.component";
import { NewsletterFormComponent } from "../newsletter-form/NewsletterFormComponent"

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./registration.view.html!";
import style from "./registration.view.css!text";

@Component( {
	selector: "registration",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink, RegistrationComponent, NewsletterFormComponent ],
	providers: [ Title ],
	styles: [ style ]
} )
export default class RegistrationView implements AfterViewInit {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		this.title.setTitle( "Registration" );
		//$('.page-content').addClass('registration');

	}

	routerOnDeactivate():void {
		//$('.page-content').removeClass('registration');
	}
}
