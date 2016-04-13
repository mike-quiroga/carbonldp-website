import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";

import Carbon from "carbonldp/Carbon";

import HighlightDirective from "app/directives/HighlightDirective";

import SidebarService from "./../sidebar/service/SidebarService";
import SidebarComponent from "./../sidebar/SidebarComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective, SidebarComponent ],
	providers: [ Title, SidebarService ]
} )
export default class GettingStartedWithTheRestApiView {
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;
	protocolAndHost:string;

	private carbon:Carbon;

	constructor( element:ElementRef, title:Title, carbon:Carbon, sidebarService:SidebarService ) {
		this.element = element;
		title.setTitle( "Getting started - Rest API" );
		this.sidebarService = sidebarService;
		this.carbon = carbon;
		this.sidebarService = sidebarService;

		title.setTitle( "Getting started - Rest API" );

		this.protocolAndHost = `${ this.carbon.getSetting( "http.ssl" ) ? "https" : "http" }://${ this.carbon.getSetting( "domain" ) }`;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		this.sidebarService.build();
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

}
