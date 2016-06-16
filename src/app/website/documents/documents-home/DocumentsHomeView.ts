import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouteConfig, RouterOutlet, RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "documents-list",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )
export default class DocumentsHomeView {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		title.setTitle(  "Documents" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}
	
}
