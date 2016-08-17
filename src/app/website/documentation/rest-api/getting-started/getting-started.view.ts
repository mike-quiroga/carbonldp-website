import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import Carbon from "carbonldp/Carbon";

import { HighlightDirective } from "carbon-panel/directives/highlight.directive";

import SidebarComponent from "./../../sidebar/sidebar.component";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./getting-started.view.html!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective, SidebarComponent ]
} )
export class GettingStartedView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;
	private protocolAndHost:string;

	private carbon:Carbon;
	private contentReady:boolean = false;

	constructor( element:ElementRef, carbon:Carbon ) {
		this.element = element;

		this.carbon = carbon;

		this.protocolAndHost = `${ this.carbon.getSetting( "http.ssl" ) ? "https" : "http" }://${ this.carbon.getSetting( "domain" ) }`;
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

export default GettingStartedView;
