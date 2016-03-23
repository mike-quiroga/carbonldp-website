import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";

import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from "jquery";
import "semantic-ui/semantic";
import SidebarComponent from "./../../sidebar/SidebarComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
	directives: [ CORE_DIRECTIVES, CodeMirrorComponent.Class, SidebarComponent ],
	providers: [ Title ],
} )
export default class GettingStartedView {
	element:ElementRef;
	$element:JQuery;
	title:Title;

	contentReady:boolean = false;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Getting started - Rest API" );
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
