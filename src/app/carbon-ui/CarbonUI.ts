import {
	Component, View,
	CORE_DIRECTIVES, FORM_DIRECTIVES,
	ElementRef,NgStyle
} from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import "./style.css!";


@Component( {
	selector: 'carbon-ui',
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES ]
} )
export default class CarbonUI {
	static parameters = [ [ ElementRef ] ];


	element:ElementRef;
	$element:JQuery;

	mainMenu:JQuery;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.mainMenu = this.$element.find( ".mainhead .menu " );
		console.log( this.mainMenu );
		let x = this.mainMenu.find( ".item" ).tab();
		console.log( x );
		//this.$element.find( "carbon-ui .mainhead .menu .item" ).tab();
	}
}