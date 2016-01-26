import { Injectable } from 'angular2/angular2';
import { CORE_DIRECTIVES, Component, Input, Output, ElementRef, SimpleChange, EventEmitter } from 'angular2/angular2';

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
	directives: [ CORE_DIRECTIVES ]
} )
@Injectable()
export default class SidebarComponent {
	static parameters = [ [ ElementRef ], [ SidebarService ] ];
	static dependencies = SidebarComponent.parameters;

	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;
	appsList:CarbonApp = <CarbonApp>[];
	itemsList:Array<SidebarItem> = [];

	constructor( element:ElementRef, sidebarService:SidebarService ) {
		this.element = element;
		this.sidebarService = sidebarService;

		this.sidebarService.rxAddItemEmitter.subscribe(
			( item ) => {
				this.addItem( item );
			}
		);
		this.sidebarService.rxtoggleEmitter.subscribe(
			( data ) => {
				this.toggle();
			}
		);
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		//this.$element.addClass( "ui left wide sidebar visible inverted vertical menu" );
	}

	greet():void {

	}

	addItem( item:SidebarItem ):void {
		this.itemsList.push( item );
	}

	toggle():void {
		if ( this.$element.hasClass( "visible" ) ) {
			this.$element.removeClass( "visible" );
			//this.$element.parent().find( "sidebar ~ .pusher" ).css( "width", "100%" );
			this.$element.parent().find( "sidebar ~ .pusher" ).addClass( "full" );
		} else {
			this.$element.addClass( "visible" );
			this.$element.parent().find( "sidebar ~ .pusher" ).removeClass( "full" );
		}


	}


}
