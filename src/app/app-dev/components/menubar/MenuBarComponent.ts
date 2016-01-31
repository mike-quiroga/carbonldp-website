import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
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
	currentUrl:string = "";


	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	updateBreadcrumbs():void {
		//let currentUrl:Array<string> = this.router.parent.lastNavigationAttempt.split( "/" );
		let currentPathname:Array<string> = location.pathname.split( "/" ), exists = false;
		this.breadCrumbs = [];
		currentPathname.forEach( ( s ) => {
			this.router.recognize( "/" + s ).then(
				( instruction ) => {
					exists = false;
					if ( instruction ) {
						this.breadCrumbs.forEach( ( breadcrumb )=> {
							if ( s == breadcrumb.url ) {
								exists = true;
							}
						} );
						if ( ! exists ) {
							this.breadCrumbs.push( {
								url: s,
								displayName: instruction.component.routeData.get( "displayName" ),
								alias: instruction.component.routeData.get( "alias" )
							} );
						}
					}
				},
				( error )=> {
					console.error( error );
				}
			);

		} );

	}

	isActive( route:string ):boolean {
		let instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
		//console.log( route );
		//return false;
	}

	afterContentChecked():void {
		if ( this.currentUrl != window.location.href ) {
			this.updateBreadcrumbs();
			this.currentUrl = window.location.href;
		}
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}