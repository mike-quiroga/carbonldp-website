import { Component, Input, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import CarbonApp from "../carbon-app/CarbonApp";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'carbon-app-thumbnail',
	template: template,
	directives: [ CORE_DIRECTIVES ]
} )
export default class CarbonAppView {
	static parameters = [ [ ElementRef ] ];

	element:ElementRef;
	$element:JQuery;
	@Input() carbonApp:CarbonApp;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( "App :%o", this.carbonApp )
	}
}