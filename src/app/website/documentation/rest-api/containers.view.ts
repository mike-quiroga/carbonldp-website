import { Component, ElementRef, AfterViewInit } from "@angular/core";
import $ from "jquery";
import "semantic-ui/semantic";

import template from "./containers.view.html!";

@Component( {
	selector: "rest-containers",
	template: template,
} )
export class ContainersView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;
	private contentReady:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		this.initializeSidebar();
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	initializeSidebar():void {
		window.setTimeout( () => {
			this.contentReady = true;
		}, 0 );
	}
}
export default ContainersView;

