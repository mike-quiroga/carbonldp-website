import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";

import SidebarComponent from "./../sidebar/SidebarComponent";

import HighlightDirective from "carbon-panel/directives/highlight.directive";

import template from "./object-model.view.html!";

@Component( {
	selector: "object-model",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent, HighlightDirective, RouterLink ],
	providers: [ Title ],
} )
export class ObjectModelView implements AfterViewInit {
	contentReady:boolean = false;

	private element:ElementRef;
	private $element:JQuery;
	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, title:Title, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.changeDetector = changeDetector;

		title.setTitle( "Object Model - JavaScript SDK" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializePopUp();
		this.initializeSidebar();
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}

	initializePopUp():void {
		this.$element.find( ".ui.definition" ).popup( {
			on: "hover"
		} );
	}
}

export default ObjectModelView;
