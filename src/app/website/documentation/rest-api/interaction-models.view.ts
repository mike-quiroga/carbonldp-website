import { Component, ElementRef, AfterViewInit } from "@angular/core";

import "semantic-ui/semantic";

import template from "./interaction-models.view.html!";

@Component( {
	selector: "interaction-models",
	template: template,
} )
export class InteractionModelsView implements AfterViewInit {
	element:ElementRef;
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

export default InteractionModelsView;

