import {Component, ElementRef, ChangeDetectorRef} from "@angular/core";
import {Title} from "@angular/platform-browser";

//import HighlightDirective from "app/directives/HighlightDirective";
import { HighlightDirective } from "carbon-panel/directives/highlight.directive";
import SidebarComponent from "./../../sidebar/SidebarComponent";

import highlight from "highlight.js";
import "highlight.js/styles/tomorrow-night.css!";

import template from "./template.html!";

@Component( {
	selector: "contexts",
	template: template,
	directives: [ SidebarComponent, HighlightDirective ],
	providers: [ Title ],
} )
export default class ContextsView {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	contentReady:boolean = false;

	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, title:Title, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.title = title;
		this.changeDetector = changeDetector;
	}

	routerOnActivate():void {
		this.title.setTitle( "Contexts - JavaScript SDK" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializeTabs();
		this.highlightCode();
		this.initializeSidebar();
		this.initializePopUp();
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeTabs():void {
		this.$element.find( ".tabular.menu .item" ).tab();
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

	initializePopUp():void {
		$( ".ui.definition" )
			.popup( {
				on: "hover"
			} );
	}
}
