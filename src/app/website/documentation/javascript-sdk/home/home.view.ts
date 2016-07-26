import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./home.view.html!";
import style from "./home.view.css!text";

@Component( {
	selector: "home",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )
export default class HomeView {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$element.find( ".doc-categories a[href]" ).on( "click", this.scrollTo );
	}

	routerOnActivate():void {
		this.title.setTitle( "JavaScript SDK Documentation" );
	}
}
