import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import SidebarComponent from "./../../sidebar/SidebarComponent";
import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "interaction-models",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent ],
	providers: [ Title ],
} )
export default class InteractionModelsView {
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
		this.title.setTitle( "Interaction models" );
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}

