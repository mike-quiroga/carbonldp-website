import { Component, ElementRef, ChangeDetectorRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES } from "angular2/router";
import { Title } from "angular2/platform/browser";

import SidebarComponent from "./../../sidebar/SidebarComponent";

import highlight from "highlight.js";
import "highlight.js/styles/tomorrow-night.css!";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "getting-started",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, SidebarComponent ],
	providers: [ Title ],
} )
export default class GettingStartedView {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	contentReady:boolean = false;

	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, title:Title, changeDetector:ChangeDetectorRef ) {
		this.element = element;

		this.title = title;
		this.title.setTitle( "Getting started - JavaScript SDK" );

		this.changeDetector = changeDetector;

	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializeTabs();
		this.highlightCode();
		this.initializeSidebar();
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeTabs():void {
		this.$element.find( ".tabular.menu .item" ).tab();
	}

	highlightCode():void {
		this.$element.find( "pre code.highlighted" ).each( function( index:number ):void {
			highlight.highlightBlock( this );
		} );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}
