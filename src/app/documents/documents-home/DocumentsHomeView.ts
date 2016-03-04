import {Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Title } from "angular2/platform/browser";
import {RouteConfig, RouterOutlet, RouterLink} from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";


@Component( {
	selector: "documents-list",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ]
} )
export default class DocumentsHomeView {
	static parameters = [ [ ElementRef ], [ Title ] ];


	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Documents" );
	}


	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}