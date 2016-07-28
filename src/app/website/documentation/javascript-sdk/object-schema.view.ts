import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import { RouterLink, OnActivate } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";

import HighlightDirective from "carbon-panel/directives/highlight.directive";

import SidebarComponent from "./../sidebar/SidebarComponent";

import template from "./object-schema.view.html!";

@Component( {
	selector: "object-schema",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent, HighlightDirective, RouterLink ],
	providers: [ Title ],
} )
export class ObjectModelView implements AfterViewInit, OnActivate {

	contentReady:boolean = false;

	private element:ElementRef;
	private title:Title;
	private changeDetector:ChangeDetectorRef;

	constructor( element:ElementRef, title:Title, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.title = title;
		this.changeDetector = changeDetector;
	}

	ngAfterViewInit():void {
		this.initializeSidebar();
	}

	routerOnActivate():void {
		this.title.setTitle( "Object Schema - JavaScript SDK" );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

export default ObjectModelView;
