import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouterLink, OnActivate } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./javascript-sdk.view.html!";
import style from "./javascript-sdk.view.css!text";

@Component( {
	selector: "javascript-sdk",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )
export class JavaScriptSDKView implements AfterViewInit, OnActivate {

	private element:ElementRef;
	private $element:JQuery;
	private title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		this.title.setTitle( "JavaScript SDK Documentation" );
	}
}

export default JavaScriptSDKView;
