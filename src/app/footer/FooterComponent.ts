import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import './style.css!';

@Component({
	selector: 'footer',
	template: template,
	directives: [ ]
})
export default class FooterComponent {
	static parameters = [[ ElementRef ]];

	element:ElementRef;
	$element;

	constructor( element: ElementRef ){
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );

		this.addSocialButtonsAnimations();
	}

	onDestroy():void {
		this.$element.find( '.social-icons .icon' ).unbind( 'mouseenter', triggerPulseTransition );
	}

	addSocialButtonsAnimations():void {
		this.$element.find( '.social-icons .icon' ).mouseenter( triggerPulseTransition );
	}
}

function triggerPulseTransition():void {
	var $element = $( this );
	$element.transition('pulse');
}