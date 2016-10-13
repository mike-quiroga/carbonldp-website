import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";

import template from "./object-schema.view.html!";

@Component( {
	selector: "object-schema",
	template: template,
} )
export class ObjectSchemaView implements AfterViewInit {

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

export default ObjectSchemaView;
