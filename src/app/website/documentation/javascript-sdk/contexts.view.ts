import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { RouterLink, OnActivate } from "@angular/router-deprecated";

import { HighlightDirective } from "carbon-panel/directives/highlight.directive";
import SidebarComponent from "./../sidebar/SidebarComponent";

import template from "./contexts.view.html!";

@Component( {
	selector: "contexts",
	template: template,
	directives: [ SidebarComponent, HighlightDirective, RouterLink ],
	providers: [ Title ],
} )
export class ContextsView implements AfterViewInit, OnActivate {
	contentReady:boolean = false;

	private element:ElementRef;
	private $element:JQuery;
	private title:Title;
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
		this.initializeSidebar();
		this.initializePopUp();
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

export default ContextsView;
