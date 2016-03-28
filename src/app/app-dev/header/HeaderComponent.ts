import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";
import Cookies from "js-cookie";

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
	$element:JQuery;
	private cookiesHandler:Cookies;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
		this.cookiesHandler = Cookies;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createDropdownMenus();
	}

	isActive( route:string ):boolean {
		let instruction:Instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	createDropdownMenus():void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover",
		} );
	}

	logOut():void {
		this.cookiesHandler.remove( "carbon_jwt" );
		this.router.navigate( [ "AppDevLogin" ] );
	}
}
