import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouterLink, OnActivate } from "@angular/router-deprecated";

import { RegisterComponent } from "./register.component";
import { NewsletterFormComponent } from "../newsletter-form/NewsletterFormComponent"

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./register.view.html!";
import style from "./register.view.css!text";

@Component( {
	selector: "register",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink, RegisterComponent, NewsletterFormComponent ],
	providers: [ Title ],
	styles: [ style ]
} )
export class RegisterView implements AfterViewInit, OnActivate {

	private element: ElementRef;
	private $element: JQuery;
	private title: Title;

	constructor( element: ElementRef, title: Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate(): void {
		this.title.setTitle( "Register" );

	}

}

export default RegisterView;
