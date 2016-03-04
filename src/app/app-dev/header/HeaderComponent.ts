/// <reference path="./../../../../typings/typings.d.ts" />
import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";


import template from "./template.html!";
import "./style.css!";
@Component( {
	selector: "header",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class HeaderComponent {
	router:Router;
	element:ElementRef;
	$element;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createDropdownMenus();
		this.createCollapsableMenus();
	}

	isActive( route:string ):boolean {
		let instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	createDropdownMenus():void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover"
		} );
	}

	createCollapsableMenus():void {
		let verticalMenu:JQuery = this.$element.find( ".ui.vertical.menu" );
		this.$element.find( ".menu.item.open" ).on( "click", function ( e ) {
			e.preventDefault();
			verticalMenu.toggle();
		} );
		verticalMenu.toggle();
	}
}