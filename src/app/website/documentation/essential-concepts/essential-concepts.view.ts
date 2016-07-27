import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./essential-concepts.view.html!";
import style from "./essential-concepts.view.css!text";

@Component( {
	selector: "essential-concepts",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )
export default class EssentialComponentsView {
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
		this.title.setTitle( "Essential Concepts Documentation" );
	}
}
