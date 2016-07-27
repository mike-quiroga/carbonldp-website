import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import SidebarComponent from "./../../sidebar/SidebarComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "rest-object-model",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent ],
	providers: [ Title ],
} )
export default class RESTObjectModelView {
	element:ElementRef;
	$element:JQuery;
	title:Title;
	private contentReady:boolean = false;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeSidebar();
	}

	routerOnActivate():void {
		this.title.setTitle( "REST API Object Model" );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

