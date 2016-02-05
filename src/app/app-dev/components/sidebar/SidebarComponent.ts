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
	appsList:CarbonApp = <CarbonApp>[];
	itemsList:Array<SidebarItem> = [];

	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;

		this.sidebarService.addItemEmitter.subscribe(
			( item ) => {
				this.addItem( item );
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
	}


	addItem( item:SidebarItem ):void {
		this.itemsList.push( item );
	}

	toggle():void {
		this.$element.sidebar( 'toggle' );
	}


}
