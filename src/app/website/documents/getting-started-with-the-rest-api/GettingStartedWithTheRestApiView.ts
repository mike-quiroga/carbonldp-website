import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";
import SidebarService from "./../sidebar/service/SidebarService";

import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from "jquery";
import "semantic-ui/semantic";
import SidebarComponent from "./../sidebar/SidebarComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
	directives: [ CORE_DIRECTIVES, CodeMirrorComponent.Class, SidebarComponent ],
	providers: [ Title, SidebarService ]
} )
export default class GettingStartedWithTheRestApiView {
	element:ElementRef;
	$element:JQuery;
	title:Title;
	sidebarService:SidebarService;
	protocolAndHost:string;

	constructor( element:ElementRef, title:Title, sidebarService:SidebarService ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Getting started - Rest API" );
		this.sidebarService = sidebarService;
		
		var location = this.getLocation(window.location.href);
		this.protocolAndHost = location.protocol + "//" + location.host;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		this.sidebarService.build();
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}
	
	getLocation(href):ElementRef {
		var location = document.createElement("a");
	    location.href = href;
	    // IE doesn't populate all link properties when setting .href with a relative URL,
	    // however .href will return an absolute URL which then can be used on itself
	    // to populate these additional fields.
	    if (location.host == "") {
	      location.href = location.href;
	    }
	    return location;
	}
	
}
