import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { Location } from "@angular/common";
import { ROUTER_DIRECTIVES, OnActivate } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";

import HighlightDirective from "carbon-panel/directives/highlight.directive";
import { SUI_COMPONENTS } from "carbon-panel/semantic";

import SidebarComponent from "../sidebar/sidebar.component";

import template from "./getting-started.view.html!";
import style from "./getting-started.view.css!text";

@Component( {
	selector: "getting-started",
	template: template,
	styles: [ style ],
	directives: [ ROUTER_DIRECTIVES, SidebarComponent, HighlightDirective, SUI_COMPONENTS, ],
	providers: [ Title ],
} )
export class GettingStartedView implements AfterViewInit, OnActivate {
	contentReady:boolean = false;

	private element:ElementRef;
	private $element:JQuery;
	private title:Title;
	private location:Location;
	private changeDetector:ChangeDetectorRef;
	private selectedLanguage:number = 0;

	constructor( element:ElementRef, title:Title, location:Location, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.title = title;
		this.location = location;
		this.changeDetector = changeDetector;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeSidebar();
	}

	routerOnActivate():void {
		this.title.setTitle( "Getting started - JavaScript SDK" );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}

	selectLanguage( language:number ):void {
		this.selectedLanguage = language;
	}
}

export default GettingStartedView;
