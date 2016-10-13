import { Component, ElementRef, AfterViewInit } from "@angular/core";

import "semantic-ui/semantic";

import template from "./community-and-support.view.html!";

@Component( {
	template: template,
} )

export class CommunityAndSupportView implements AfterViewInit {

	private element:ElementRef;
	private $element:JQuery;

	private contentReady:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		window.setTimeout( () => this.contentReady = true, 0 );
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

}

export default CommunityAndSupportView;
