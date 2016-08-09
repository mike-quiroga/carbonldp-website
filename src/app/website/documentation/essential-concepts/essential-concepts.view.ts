import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./essential-concepts.view.html!";
import style from "./essential-concepts.view.css!text";

@Component( {
	selector: "essential-concepts",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ]
} )

export class EssentialConceptsView implements AfterViewInit {

	private element: ElementRef;
	private $element: JQuery;

	constructor( element: ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
	}

}

export default EssentialConceptsView;
