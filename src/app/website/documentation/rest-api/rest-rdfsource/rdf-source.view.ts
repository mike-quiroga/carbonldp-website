import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import { HighlightDirective } from "carbon-panel/directives/highlight.directive";

import SidebarComponent from "./../../sidebar/sidebar.component";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./RDF-source.view.html!";

@Component( {
	selector: "rdfsource",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective, SidebarComponent ]
} )
export class RDFSourceView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;

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

export default RDFSourceView;

