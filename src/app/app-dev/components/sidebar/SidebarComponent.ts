import { Component, Input, Output, Injectable, ElementRef, SimpleChange, EventEmitter } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import SidebarService from './service/SidebarService'
import CarbonApp from 'app/app-dev/my-apps/carbon-app/CarbonApp'
import SidebarItem from "./SidebarItem";

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'sidebar',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
@Injectable()
export default class SidebarComponent {
	static parameters = [ [ Router ], [ ElementRef ], [ SidebarService ] ];
	static dependencies = SidebarComponent.parameters;

	router:Router;
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;
	carbonApps:CarbonApp[] = [];

	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;

		this.sidebarService.addCarbonAppEmitter.subscribe(
			( item ) => {
				this.addCarbonApp( item );
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
		this.$element.sidebar( {
			context: 'app-dev > div.page-content',
			transition: 'push',
			closable: false,
			dimPage: false,
			scrollLock: true
		} );
		this.refreshAccordion();
	}


	addCarbonApp( app:CarbonApp ):void {
		! this.slugExists( app.slug ) ? this.carbonApps.push( app ) : null;
	}

	toggle():void {
		this.$element.sidebar( 'toggle' );
	}

	refreshAccordion():void {
		this.$element.accordion( {
			selector: {
				trigger: '.item.carbonApp, .item.carbonApp .title',
				title: '.title',
			},
			exclusive: false,
		} );
	}

	removeCarbonApp( id:number ):void {
		this.carbonApps.splice( id, 1 );
	}

	isActive( slug:string ):boolean {
		let url:string[] = location.pathname.split( "/" );
		return url.indexOf( slug ) > - 1;
	}

	slugExists( slug:string ):boolean {
		return ! (typeof this.carbonApps.find( _app => _app.slug == slug ) === "undefined");
	}
}
