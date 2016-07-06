import { Component, ElementRef, Inject } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router, Instruction } from "@angular/router-deprecated";

import { AuthService } from "angular2-carbonldp/services";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "header",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class HeaderComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;

	private authService:AuthService.Class;

	constructor( router:Router, element:ElementRef, @Inject( AuthService.Token ) authService:AuthService.Class ) {
		this.router = router;
		this.element = element;
		this.authService = authService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createDropdownMenus();
		this.createCollapsableMenus();
	}

	isActive( route:string ):boolean {
		let instruction:Instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	createCollapsableMenus():void {
		let verticalMenu:JQuery = this.$element.find( ".ui.vertical.menu" );
		this.$element.find( ".item.open" ).on( "click", function ( e ) {
			e.preventDefault();
			verticalMenu.toggle();
		} );
		verticalMenu.toggle();
	}

	createDropdownMenus():void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover",
		} );
	}

	logOut():void {
		this.authService.logout();
		this.router.navigate( [ "/AppDevLogin" ] );
	}
}
