import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import { RouterLink } from "@angular/router-deprecated";

import HighlightDirective from "carbon-panel/directives/highlight.directive";

import SidebarComponent from "./../sidebar/sidebar.component";

import template from "./object-schema.view.html!";

@Component( {
	selector: "object-schema",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent, HighlightDirective, RouterLink ],
} )
export class ObjectModelView implements AfterViewInit {

	contentReady:boolean = false;

	private element:ElementRef;
	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.changeDetector = changeDetector;
	}

	ngAfterViewInit():void {
		this.initializeSidebar();
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

export default ObjectModelView;
