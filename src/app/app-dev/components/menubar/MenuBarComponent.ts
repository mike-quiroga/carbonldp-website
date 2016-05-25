import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import SidebarService from "./../sidebar/service/SidebarService";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "menu-bar",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class MenuBarComponentComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;
	clickable:boolean = false;

	breadCrumbs:Array<any> = [];
	instructions:Instruction[] = [];


	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;
		this.router.parent.subscribe( ( url )=> {
			this.updateBreadcrumbs( url );
		} );
		this.sidebarService.toggleMenuButtonEmitter.subscribe(
			() => {
				this.toggleMenuButton();
			}
		);
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	toggleMenuButton():void {
		this.clickable = ! this.clickable;
	}

	updateBreadcrumbs( url:string ):void {
		this.instructions = [];
		this.breadCrumbs = [];
		let workingInstruction:Instruction;
		this.router.recognize( url ).then(
			( instruction )=> {
				if ( instruction ) {
					workingInstruction = instruction;
					while ( workingInstruction.child ) {
						this.addInstruction( workingInstruction );
						workingInstruction = workingInstruction.child;
					}
					if ( ! workingInstruction.child && ! ! workingInstruction.urlPath ) {
						this.addInstruction( workingInstruction );
					}
				}
			}
		);
	}

	getRouteAlias():any {
		let alias:any[] = [], params:{name:string} = { name: "" };
		this.instructions.forEach(
			( instruction )=> {
				if ( instruction ) {
					alias.push( instruction.component.routeData.data[ "alias" ] );
					params = instruction.component.routeData.data[ "params" ];
					if ( ! ! params ) {
						alias.push( { [params.name]: instruction.urlPath } );
					}
				}
			}
		);
		return alias;
	}

	addInstruction( workingInstruction:Instruction ):void {
		this.instructions.push( workingInstruction );
		this.breadCrumbs.push( {
			url: workingInstruction.urlPath,
			displayName: workingInstruction.component.routeData.data[ "displayName" ],
			alias: this.getRouteAlias(),
			friendlyAlias: this.getFriendlyAlias()
		} );
	}

	getFriendlyAlias():any {
		let friendlyURL:string = "";
		this.instructions.forEach(
			( instruction )=> {
				if ( instruction ) {
					friendlyURL += instruction.component.routeData.data[ "alias" ];
					friendlyURL += instruction.child ? "/" : "";
				}
			}
		);
		return friendlyURL;
	}

	isActive( route:any ):boolean {
		let instruction = this.router.generate( route );
		let router:Router = this.router;
		while ( instruction.child ) {
			// TODO: Change this to use a non private variables implementation.
			instruction = instruction.child;
			if ( typeof router._childRouter === "undefined" || router._childRouter === null ) continue;
			if ( typeof router._childRouter._currentInstruction === "undefined" || router._childRouter._currentInstruction === null ) continue;
			router = router._childRouter;
		}
		return router.isRouteActive( instruction );
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}