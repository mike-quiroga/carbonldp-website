import { Component, ElementRef, Input, Inject } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, Router, Instruction } from "angular2/router";

import { AuthService } from "angular2-carbonldp/services";

import $ from "jquery";
import "semantic-ui/semantic";

import LoginComponent from "app/components/login/LoginComponent";

import template from "./template.html!";
import "./style.css!";
@Component( {
	selector: "header",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, LoginComponent, ],
} )
export default class HeaderComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	authService:AuthService.Class;

	constructor( router:Router, element:ElementRef, @Inject( AuthService.Token ) authService:AuthService.Class ) {
		this.router = router;
		this.element = element;
		this.authService = authService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createDropdownMenus();
		this.createCollapsableMenus();
		this.createLoginPopUp();
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

	createCollapsableMenus():void {
		let verticalMenu:JQuery = this.$element.find( ".ui.vertical.menu" );
		this.$element.find( ".right.menu.open" ).on( "click", function ( e ) {
			e.preventDefault();
			verticalMenu.toggle();
		} );
		verticalMenu.toggle();
	}

	createLoginPopUp():void {
		this.$element.find( ".computer.tablet .login.item" ).popup( {
			popup: this.$element.find( ".login.popup" ),
			hoverable: false,
			position: "bottom right",
			on: "click",
			preserve: true,
			transition: "pulse",
			hideOnScroll: false,
			closable: false,
		} );
	}

	ngAfterViewChecked():void {
		this.createLoginPopUp();
	}

	logOut():void {
		this.authService.logout();
		this.router.navigate( [ "/Home" ] );
	}
}
