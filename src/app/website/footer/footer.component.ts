import { Component, ElementRef, AfterViewInit, OnDestroy } from "@angular/core";
import { Angulartics2On } from "angulartics2/src/core/angulartics2On";
import { Angulartics2 } from "angulartics2";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./footer.component.html!";
import style from "./footer.component.css!text";

@Component( {
	selector: "footer",
	template: template,
	directives: [ Angulartics2On ],
	styles: [ style ],
} )
export class FooterComponent implements AfterViewInit, OnDestroy {
	private element:ElementRef;
	private $element;
	private date = new Date();
	private year = this.date.getFullYear();


	constructor( element:ElementRef ) {
		this.element = element;

	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.addSocialButtonsAnimations();
	}

	ngOnDestroy():void {
		this.$element.find( ".social-icons .icon" ).unbind( "mouseenter", triggerPulseTransition );
	}

	addSocialButtonsAnimations():void {
		this.$element.find( ".social-icons .icon" ).mouseenter( triggerPulseTransition );
	}

}

function triggerPulseTransition():void {
	var $element = $( this );
	$element.transition( "pulse" );
}

export default FooterComponent;