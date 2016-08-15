import { Component, ElementRef, Inject, AfterViewInit, AfterViewChecked } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router, Instruction } from "@angular/router-deprecated";

import { AuthService } from "angular2-carbonldp/services";

import $ from "jquery";
import "semantic-ui/semantic";

import { LoginComponent } from "carbon-panel/login.component";

import template from "./header.component.html!";
import style from "./header.component.css!text";

@Component( {
	selector: "header",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, LoginComponent, ],
	styles: [ style ],
} )
export class HeaderComponent implements AfterViewInit, AfterViewChecked {
	private router:Router;
	private element:ElementRef;
	private $element:JQuery;
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

export default HeaderComponent;