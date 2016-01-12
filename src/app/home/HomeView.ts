import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import CarbonLogoComponent from 'app/components/logo/CarbonLogoComponent';

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'home',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CarbonLogoComponent, CodeMirrorComponent.Class ]
} )
export default class HomeView {
	static parameters = [ [ Router ], [ ElementRef ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;
	$mainMenu:JQuery;
	$carbonLogo:JQuery;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$mainMenu = $( 'header > .menu' );
		//this.$carbonLogo = this.$element.find( 'carbon-logo' );
		this.$carbonLogo = this.$element.find( 'img.carbon-logo' );

		this.hideMainMenu();
		this.createDropdownMenus();
		this.addMenuVisibilityHandlers();
	}

	onDeactivate():void {
		this.removeMenuVisibilityHandlers();
		this.showMainMenu();
	}

	isActive( route:string ):boolean {
		let instruction = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	showMainMenu():void {
		if ( this.$mainMenu.is( ':visible' ) ) return;
		this.toggleMainMenu();
	}

	hideMainMenu():void {
		if ( ! this.$mainMenu.is( ':visible' ) ) return;
		this.toggleMainMenu();
	}

	toggleMainMenu():void {
		this.$mainMenu.transition( 'fade down' );
	}

	createDropdownMenus():void {
		this.$element.find( '.ui.dropdown' ).dropdown( {
			on: 'hover'
		} );
	}

	addMenuVisibilityHandlers():void {
		var view:HomeView = this;
		this.$carbonLogo.visibility( {
			once: false,
			onBottomPassedReverse: function ( calculations ) {
				view.hideMainMenu();
			},
			onBottomPassed: function ( calculations ) {
				view.showMainMenu();
			}
		} );
	}

	removeMenuVisibilityHandlers():void {
		this.$carbonLogo.visibility( 'destroy' );
	}

	scrollTo( event:any ):boolean {
		let
			id:string = $( this ).attr( 'href' ).replace( '#', '' ),
			$element:JQuery = $( '#' + id ),
			position:number = $element.offset().top - 80
			;
		$element.addClass( 'active' );
		$( 'html, body' ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = '#' + id;
		event.stopImmediatePropagation();
		event.preventDefault();
		return false;
	}

}