import { Component, ElementRef, AfterViewInit } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./quick-start-guide.view.html!";

@Component( {
	template: template
} )
export class QuickStartGuideView implements AfterViewInit {

	private element:ElementRef;
	private $element:JQuery;

	private contentReady:boolean = false;

	private randomString:string;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.randomString = this.generateRandomString( 32, "aA#" );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		window.setTimeout( () => this.contentReady = true, 0 );
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	generateRandomString( length, chars ) {
		let mask = "";
		if( chars.indexOf( "a" ) > - 1 ) mask += "abcdefghijklmnopqrstuvwxyz";
		if( chars.indexOf( "A" ) > - 1 ) mask += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if( chars.indexOf( "#" ) > - 1 ) mask += "0123456789";
		if( chars.indexOf( "!" ) > - 1 ) mask += "~`!@#$%^&*()_+-={}[]:\";'<>?,./|\\";

		let result = "";
		for ( let i = length; i > 0; -- i ) result += mask[ Math.floor( Math.random() * mask.length ) ];

		return result;
	}
}
export default QuickStartGuideView;