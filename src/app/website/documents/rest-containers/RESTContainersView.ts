import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";

import HighlightDirective from "app/directives/HighlightDirective";

import SidebarService from "./../sidebar/service/SidebarService";
import SidebarComponent from "./../sidebar/SidebarComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "rest-containers",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective, SidebarComponent ],
	providers: [ Title, SidebarService ],
} )
export default class RESTContainersView {
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;

	constructor( element:ElementRef, title:Title, sidebarService:SidebarService ) {
		this.element = element;
		this.sidebarService = sidebarService;
		title.setTitle( "Containers" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
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

