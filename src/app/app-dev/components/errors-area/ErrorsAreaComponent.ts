import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import ErrorsAreaService from "./service/ErrorsAreaService";

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
	messages:Message[] = [];
	errorsAreaService:ErrorsAreaService;

	constructor( element:ElementRef, errorsAreaService:ErrorsAreaService ) {
		this.element = element;
		this.$element = $( element );
		this.errorsAreaService = errorsAreaService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.errorsAreaService.addErrorEmitter.subscribe(
			( message ):void => {
				this.messages.push( message );
			}
		);
	}

	closeMessage( evt:any ):void {
		$( evt.srcElement ).closest( ".ui.message" ).transition( "fade" );
	}

}
export interface Message {
	title:string;
	content:string;
	statusCode:string;
	statusMessage:string;
	endpoint:string;
}
