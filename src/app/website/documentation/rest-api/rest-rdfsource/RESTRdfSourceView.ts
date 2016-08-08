import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import { HighlightDirective } from "carbon-panel/directives/highlight.directive";

import SidebarComponent from "./../../sidebar/SidebarComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "rest-rdfsource",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective, SidebarComponent ]
} )
export default class RESTRdfSourceView {
	element:ElementRef;
	$element:JQuery;

	private contentReady:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		this.initializeSidebar();
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}


	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

