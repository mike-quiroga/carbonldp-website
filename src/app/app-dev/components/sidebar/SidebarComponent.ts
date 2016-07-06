import { Component, Input, Output, Injectable, ElementRef, SimpleChange, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES, Location } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import SidebarService from "./service/SidebarService"
import { App } from "app/app-dev/my-apps/app/app"
import SidebarItem from "./SidebarItem";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "sidebar",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class SidebarComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;
	apps:App[] = [];
	location:Location;

	constructor( router:Router, element:ElementRef, location:Location, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;
		this.location = location;

		this.sidebarService.addAppEmitter.subscribe(
			( item ) => {
				this.addApp( item );
			}
		);
		this.sidebarService.toggleEmitter.subscribe(
			() => {
				this.toggle();
			}
		);
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.refreshAccordion();
	}


	addApp( app:App ):void {
		! this.slugExists( app.slug ) ? this.apps.push( app ) : null;
	}

	toggle():void {
		this.sidebarService.toggleMenuButtonEmitter.emit( null );
		if ( this.$element.is( ":visible" ) ) {
			this.$element.animate( { "width": "0" }, 400, () => {
				this.$element.hide();
			} );
		} else {
			this.$element.show();
			this.$element.animate( { "width": "300px" }, 400 );
		}
	}

	refreshAccordion():void {
		this.$element.accordion( {
			selector: {
				trigger: ".item.app, .item.app .title",
				title: ".title",
			},
			exclusive: false
		} );
	}

	removeApp( id:number ):void {
		this.apps.splice( id, 1 );
	}

	isActive( slug:any, fullRoute?:boolean ):boolean {
		switch ( typeof slug ) {
			case "string":
				let url:string[] = this.location.path().split( "/" );
				if ( fullRoute ) {
					return url.indexOf( slug ) > - 1;
				} else {
					return url[ url.length - 1 ].indexOf( slug ) > - 1;
				}
			case "object":
				// TODO: Change this to use a non private variables implementation.
				let instruction = this.router.generate( slug );
				let router = this.router;
				if ( ! fullRoute ) {
					while ( instruction.child ) {
						instruction = instruction.child;
						if ( typeof router._childRouter === "undefined" || router._childRouter === null ) continue;
						if ( typeof router._childRouter._currentInstruction === "undefined" || router._childRouter._currentInstruction === null ) continue;
						router = router._childRouter;
					}
				}
				return router.isRouteActive( instruction );
			default:
				return false;
		}
	}

	slugExists( slug:string ):boolean {
		return ! (typeof this.apps.find( _app => _app.slug == slug ) === "undefined");
	}
}
