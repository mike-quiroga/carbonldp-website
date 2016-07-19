import {Component, ElementRef, ChangeDetectorRef} from "@angular/core";
import {Location} from "@angular/common";
import {ROUTER_DIRECTIVES, Router} from "@angular/router-deprecated";
import {Title} from "@angular/platform-browser";

import SidebarComponent from "./../../sidebar/SidebarComponent";

import HighlightDirective from "carbon-panel/directives/highlight.directive";
import highlight from "highlight.js";
import "highlight.js/styles/tomorrow-night.css!";

import template from "./template.html!";


@Component( {
	selector: "getting-started",
	template: template,
	directives: [ ROUTER_DIRECTIVES, SidebarComponent, HighlightDirective ],
	providers: [ Title ],
} )
export default class GettingStartedView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	title:Title;
	contentReady:boolean = false;

	private location:Location;
	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, title:Title, router:Router, location:Location, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.title = title;
		this.router = router;
		this.location = location;
		this.changeDetector = changeDetector;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializeTabs();
		this.highlightCode();
		this.$element.find( ".sectionlink a[href]" ).on( "click", this.scrollTo );
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
