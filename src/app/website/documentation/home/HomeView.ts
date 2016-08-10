import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "documents-list",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )
export default class HomeView {
	element: ElementRef;
	$element: JQuery;
	title: Title;

	constructor( element: ElementRef, title: Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
		this.$element.find( ".doc-categories a[href]" ).on( "click", this.scrollTo );
	}

	routerOnActivate(): void {
		this.title.setTitle( "Documentation" );
	}


	scrollTo( event: any ): boolean {
		let id: string = $( event.currentTarget ).attr( "href" ).replace( "#", "" );
		let $element: JQuery = $( "#" + id );
		let position: number = $element.offset().top - 100;

		$( "html, body" ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = "#" + id;
		event.stopImmediatePropagation();
		event.preventDefault();

		return false;
	}
}
