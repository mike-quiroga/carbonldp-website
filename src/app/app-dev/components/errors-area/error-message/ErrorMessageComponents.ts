import { Component, ElementRef, Input } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import { Message } from "./../ErrorsAreaComponent";

import template from "./template.html!";

@Component( {
	selector: "error-message",
	template: template,
} )

export default class ErrorMessageComponent {

	element:ElementRef;
	$element:JQuery;

	@Input() title:string;
	@Input() content:string;
	@Input() statusCode:string;
	@Input() statusMessage:string;
	@Input() endpoint:string;
	@Input() message:Message;
	@Input() closable:boolean = false;

	constructor( elementRef:ElementRef ) {
		this.element = elementRef;
	}

	ngOnInit():void {
		if ( this.message ) this.decomposeMessage();
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	private decomposeMessage():void {
		this.title = this.message.title;
		this.content = this.message.content;
		this.statusCode = this.message.statusCode;
		this.statusMessage = this.message.statusMessage;
		this.endpoint = this.message.endpoint;
	}

	close( event:Event ):void {
		$( event.srcElement ).closest( "message" );
	}
}