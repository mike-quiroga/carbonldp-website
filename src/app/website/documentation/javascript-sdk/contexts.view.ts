import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";

import template from "./contexts.view.html!";

@Component( {
	selector: "contexts",
	template: template,
} )
export class ContextsView implements AfterViewInit {
	contentReady:boolean = false;

	private element:ElementRef;
	private changeDetector:ChangeDetectorRef;
	private selectedLanguage:number = 0;

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

	selectLanguage( language:number ):void {
		this.selectedLanguage = language;
	}
}

export default ContextsView;
