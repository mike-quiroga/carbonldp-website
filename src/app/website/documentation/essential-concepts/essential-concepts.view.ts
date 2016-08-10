import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Title } from "@angular/platform-browser";
import { RouterLink, OnActivate } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./essential-concepts.view.html!";
import style from "./essential-concepts.view.css!text";

@Component( {
	selector: "essential-concepts",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ],
	providers: [ Title ],
} )

export class EssentialConceptsView implements AfterViewInit, OnActivate {

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
		this.title.setTitle( "Essential Concepts Documentation" );
	}
}

export default EssentialConceptsView;