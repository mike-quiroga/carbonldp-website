import { Component, ElementRef } from "angular2/core";

import $ from "jquery";

import template from "./template.html!";
import "./style.css!";

@Component({
	selector: "carbon-logo",
	template: template
})
export default class CarbonLogoComponent {
	element:ElementRef;
	$element;

	constructor( element: ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.causeRedraw();

		this.setBackgroundColorBasedOnParents();
		this.setFontSize();
		this.addWindowResizeEventHandler();

		this.causeRedraw();
	}

	setFontSize():void {
		var width = this.$element.width();
		this.$element.css("font-size", width);
	}

	addWindowResizeEventHandler():void {
		$( window ).resize( () => {
			this.setFontSize();
			this.causeRedraw();
		});
	}

	causeRedraw():void {
		this.$element.hide().show(0);
	}

	setBackgroundColorBasedOnParents():void {
		var backgroundColor = this.findParentBackgroundColor();
		if( ! backgroundColor ) return;

		this.$element.find( ".electron" ).css( "border-color", backgroundColor );
		this.$element.find( ".c" ).css( "border-color", backgroundColor );
	}

	findParentBackgroundColor():string {
		var backgroundColor;
		this.$element.parents().each( function() {
			var $parent = $( this );
			backgroundColor = $parent.css( "background-color" );

			if( ! ( backgroundColor === "transparent" || backgroundColor === "rgba(0, 0, 0, 0)" ) ) return false;
		} );
		return backgroundColor;
	}
}