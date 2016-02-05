import { Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import SidebarService from "./../sidebar/service/SidebarService";

import template from './template.html!';
import "./style.css!";
import resolve = Promise.resolve;
@Component( {
	selector: 'menu-bar',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class MenuBarComponentComponent {
	static parameters = [ [ Router ], [ ElementRef ], [ SidebarService ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;

	breadCrumbs:Array<any> = [];
	instructions:Instruction[] = [];
	currentUrl:string = "";


	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	updateBreadcrumbs():void {
		//let currentUrl:Array<string> = this.router.parent.lastNavigationAttempt.split( "/" );
		let currentPathname:string[] = location.pathname.split( "/" ), exists = false;
		this.breadCrumbs = [];
		//currentPathname.forEach( ( s ) => {
		for ( var i = 0, s = ""; i < currentPathname.length; i ++ ) {
			s = currentPathname[ i ];
			this.router.recognize( "/" + s ).then(
				( instruction ) => {
					this.instructions.push( instruction );
					//exists = false;
					//if ( instruction ) {
					//	//this.breadCrumbs.forEach( ( breadcrumb )=> {
					//	//	if ( s == breadcrumb.url ) {
					//	//		exists = true;
					//	//	}
					//	//} );
					//	//if ( ! exists ) {
					//	//	this.breadCrumbs.push( {
					//	//		url: s,
					//	//		displayName: instruction.component.routeData.get( "displayName" ),
					//	//		alias: instruction.child ? instruction.component.routeData.get( "alias" ) + "/" + instruction.child.component.routeData.get( "alias" ) : instruction.component.routeData.get( "alias" )
					//	//	} );
					//	//}
					//	if ( instruction.child && instruction.child.component.routeData.get( "displayName" ) ) {
					//		console.log( "Instruction: %o\nInstruction Child Component:%o", instruction, instruction.child.component.routeData.data );
					//		this.breadCrumbs.push( {
					//			url: s,
					//			displayName: instruction.child.component.routeData.get( "displayName" ),
					//			alias: instruction.child ? instruction.child.component.routeData.get( "alias" ) + "/" + instruction.child.component.routeData.get( "alias" ) : instruction.component.routeData.get( "alias" )
					//		} );
					//	}
					//}
				},
				( error )=> {
					console.error( error );
				}
			)
				//.then(
				//	()=> {
				//		console.clear();
				//		console.log( this );
				//		console.log( currentPathname );
				//		console.log( this.instructions );
				//	}
				//)
			;
		}

	}

	isActive( route:string ):boolean {
		let instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	ngAfterContentChecked():void {
		if ( this.currentUrl != window.location.href ) {
			this.updateBreadcrumbs();
			this.currentUrl = window.location.href;
		}
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}