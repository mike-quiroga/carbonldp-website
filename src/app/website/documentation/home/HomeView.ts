import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "documents-list",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink ]
} )
export default class HomeView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$element.find( ".doc-categories a[href]" ).on( "click", this.scrollTo );
	}


	scrollTo( event:any ):boolean {
		let id:string = $( event.currentTarget ).attr( "href" ).replace( "#", "" );
		let $element:JQuery = $( "#" + id );
		let position:number = $element.offset().top - 100;

		$( "html, body" ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = "#" + id;
		event.stopImmediatePropagation();
		event.preventDefault();

		return false;
	}
}
