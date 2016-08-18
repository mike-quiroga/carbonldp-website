import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { RouterLink } from "@angular/router-deprecated";

import { HighlightDirective } from "carbon-panel/directives/highlight.directive";
import SidebarComponent from "./../sidebar/sidebar.component";

import template from "./contexts.view.html!";

@Component( {
	selector: "contexts",
	template: template,
	directives: [ SidebarComponent, HighlightDirective, RouterLink ],
} )
export class ContextsView implements AfterViewInit {
	contentReady:boolean = false;

	private element:ElementRef;
	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.changeDetector = changeDetector;
	}

	ngAfterViewInit():void {
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
