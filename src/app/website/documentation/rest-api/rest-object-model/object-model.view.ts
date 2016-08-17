import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import SidebarComponent from "./../../sidebar/sidebar.component";

import "semantic-ui/semantic";

import template from "./object-model.view.html!";

@Component( {
	selector: "object-model",
	template: template,
	directives: [ CORE_DIRECTIVES, SidebarComponent ]
} )
export class ObjectModelView implements AfterViewInit {
	private element:ElementRef;
	private contentReady:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
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
