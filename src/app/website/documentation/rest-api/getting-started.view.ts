import { Component, ElementRef, AfterViewInit } from "@angular/core";

import Carbon from "carbonldp/Carbon";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./getting-started.view.html!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
} )
export class GettingStartedView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;
	private protocolAndHost:string;

	private carbon:Carbon;
	private contentReady:boolean = false;

	private selectedLanguage:number = 0;

	private languages:{ [ name:string ]:number } = {
		trig: 0,
		jsonld: 1
	};

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

	selectLanguage( language:number ):void {
		this.selectedLanguage = language;
	}
}

export default GettingStartedView;
