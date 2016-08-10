import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink, OnActivate } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./rest-api.view.html!";
import style from "./rest-api.view.css!text";

@Component( {
	selector: "rest-api",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ]
} )

export class RESTApiView implements AfterViewInit {

	private element:ElementRef;
	private $element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}

export default RESTApiView;
