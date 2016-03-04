import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";
import SidebarService from "./../Sidebar/service/SidebarService";


import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from "jquery";
import "semantic-ui/semantic";
import SideBarComponent from "app/documents/Sidebar/SideBarComponent";

import template from "./template.html!";
import "./style.css!";


@Component( {
	selector: "getting-started-rest-api",
	template: template,
	directives: [ CORE_DIRECTIVES, CodeMirrorComponent.Class, SideBarComponent ],
	providers: [ Title, SidebarService ]
} )
export default class GettingStartedWithTheRestApiView {
	static parameters = [ [ ElementRef ], [ Title ], [ SidebarService ] ];

	element:ElementRef;
	$element:JQuery;
	title:Title;
	sidebarService:SidebarService;

	constructor( element:ElementRef, title:Title, sidebarService:SidebarService ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Getting started - Rest API" );
		this.sidebarService = sidebarService;
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