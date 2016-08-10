import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";

import SidebarComponent from "./../../sidebar/SidebarComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "rest-containers",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent ],
	providers: [ Title ],
} )
export default class RESTContainersView {
	element: ElementRef;
	$element: JQuery;
	title: Title;
	private contentReady: boolean = false;

	constructor( element: ElementRef, title: Title ) {
		this.element = element;
		this.title = title;

	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		this.initializeSidebar();
	}

	routerOnActivate(): void {
		this.title.setTitle( "Containers" );
	}

	createAccordions(): void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeSidebar(): void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

