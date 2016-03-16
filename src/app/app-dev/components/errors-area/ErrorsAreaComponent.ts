/// <reference path="./../../../../../typings/typings.d.ts" />
import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "errors-area",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )
export default class ErrorsAreaComponent {
	element:ElementRef;
	$element:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
