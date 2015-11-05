import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import CarbonLogoComponent from 'app/components/logo/CarbonLogoComponent';

import template from './template.html!';
import './style.css!';

@Component({
	selector: 'home',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonLogoComponent ]
})
export default class HomeView {
	static parameters = [[ Router ], [ ElementRef ]];

	router:Router;
	element:ElementRef;
	$element:JQuery;
	$mainMenu:JQuery;

	constructor( router:Router, element: ElementRef ){
		this.router = router;
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$mainMenu = $( 'header > .menu' );

		this.hideMainMenu();
		this.createDropdownMenus();

		var view:HomeView = this;
		this.$element.find( 'carbon-logo' ).visibility({
			once: false,
			onBottomPassedReverse: function( calculations ) {
				view.toggleMainMenu();
			},
			onBottomPassed: function( calculations ) {
				view.toggleMainMenu();
			}
		});
	}

	onDeactivate():void {
		//this.showMainMenu();
	}

	isActive( route:string ):boolean {
		let instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	showMainMenu():void {
		this.$mainMenu.show();
	}

	hideMainMenu():void {
		this.$mainMenu.hide();
	}

	toggleMainMenu():void {
		this.$mainMenu.transition( 'fade down' );
	}

	createDropdownMenus():void {
		this.$element.find( '.ui.dropdown' ).dropdown({
			on: 'hover'
		});
	}
}