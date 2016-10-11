import { Component, ElementRef, AfterViewInit } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./quick-start-guide.view.html!";

@Component( {
	template: template
} )
export class QuickStartGuideView implements AfterViewInit {

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
export default QuickStartGuideView;