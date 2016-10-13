import { Component, ElementRef, ChangeDetectorRef, AfterViewInit } from "@angular/core";
import { Location } from "@angular/common";

import template from "./getting-started.view.html!";
import style from "./getting-started.view.css!text";

@Component( {
	selector: "getting-started",
	template: template,
	styles: [ style ],
} )
export class GettingStartedView implements AfterViewInit {
	contentReady:boolean = false;

	private element:ElementRef;
	private location:Location;
	private changeDetector:ChangeDetectorRef;
	private selectedLanguage:number = 0;

	constructor( element:ElementRef, location:Location, changeDetector:ChangeDetectorRef ) {
		this.element = element;
		this.location = location;
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

	selectLanguage( language:number ):void {
		this.selectedLanguage = language;
	}
}

export default GettingStartedView;
