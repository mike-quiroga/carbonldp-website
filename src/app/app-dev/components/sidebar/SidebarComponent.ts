import { Injectable } from 'angular2/angular2';
import { CORE_DIRECTIVES, Component, Input, Output, ElementRef, Query, QueryList, SimpleChange, EventEmitter } from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import SidebarService from './service/SidebarService'

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

	counter:number = 0;

	@Input() value:string = "";
	//@Output() valueChange:EventEmitter = new EventEmitter();


	constructor( element:ElementRef, sidebarService:SidebarService ) {
		this.element = element;
		this.sidebarService = sidebarService;

	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$element.addClass( "ui left sidebar visible inverted vertical menu" );
	}

	greet():void {
		alert( "Hello from Sidebar Component" );
	}

	add():void {
		this.counter ++;
	}

	show():void {
		alert( this.counter );
	}
}
