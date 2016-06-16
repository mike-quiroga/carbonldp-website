import { Component, ElementRef, ChangeDetectorRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";

import SidebarComponent from "./../../sidebar/SidebarComponent";

import highlight from "highlight.js";
import "highlight.js/styles/tomorrow-night.css!";

import template from "./template.html!";


@Component( {
	selector: "getting-started",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, SidebarComponent ],
	providers: [ Title ],
} )
export default class GettingStartedView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	title:Title;

	contentReady:boolean = false;

	private changeDetector:ChangeDetectorRef;

	constructor( router:Router, element:ElementRef, title:Title, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.router = router;
		this.title = title;

		this.changeDetector = changeDetector;

	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializeTabs();
		this.highlightCode();

		this.initializeSidebar();
	}

	routerOnActivate():void {
		this.title.setTitle( "Getting started - JavaScript SDK" );
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeTabs():void {
		this.$element.find( ".tabular.menu .item" ).tab( {
			history: false
		} );

	}

	highlightCode():void {
		this.$element.find( "pre code.highlighted" ).each( function ( index:number ):void {
			highlight.highlightBlock( this );
		} );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}

}
