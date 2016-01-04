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
	elementsTabContent:JQuery;
	collectionsTabContent:JQuery;
	viewsTabContent:JQuery;
	modulesTabContent:JQuery;


	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.mainMenu = this.$element.find( ".mainhead .menu " );
		this.mainMenu.find( ".item" ).tab();
		this.elementsTabContent = this.$element.find( '.ui.tab[data-tab="elements"]' );
		this.collectionsTabContent = this.$element.find( '.ui.tab[data-tab="collections"]' );
		this.viewsTabContent = this.$element.find( '.ui.tab[data-tab="views"]' );
		this.modulesTabContent = this.$element.find( '.ui.tab[data-tab="modules"]' );

		this.enableCollectionsJavascript();
		this.enableViewsJavascript();
		this.enableModulesJavascript();
	}

	enableCollectionsJavascript():void {
		this.collectionsTabContent.find( ".ui.dropdown" ).dropdown();
		this.collectionsTabContent.find( ".ui.checkbox" ).checkbox();
		this.collectionsTabContent.find( ".menu a.item" ).on( "click", function () {
			if ( ! $( this ).hasClass( "dropdown" ) ) {
				$( this )
					.addClass( "active" )
					.closest( ".ui.menu" )
					.find( "item" )
					.not( $( this ) )
					.removeClass( "active" )
				;
			}
		} );
		this.collectionsTabContent.find( ".message .close" ).on( "click", function () {
			$( this ).closest( ".message" ).transition( "scale out" );
		} );
	}

	enableViewsJavascript():void {
		this.viewsTabContent.find( ".star.rating" ).rating();
		this.viewsTabContent.find( ".card .dimmer" ).dimmer( {
			on: "hover"
		} );
	}

	enableModulesJavascript():void {
		this.modulesTabContent.find( ".ui.accordion" ).accordion();
		this.modulesTabContent.find( ".ui.checkbox" ).checkbox();
		let $pageDimmer = $( ".demo.page.dimmer" ),
			$demo:JQuery = this.modulesTabContent.find( ".dimmer.demo" ),
			$showButton:JQuery = this.modulesTabContent.find( ".show.button" ),
			$pageButton:JQuery = this.modulesTabContent.find( ".page.button" ),
			$hideButton:JQuery = this.modulesTabContent.find( ".hide.button" );
		$showButton.on( "click", function () {
			$( this ).closest( '.demo' ).find( '.segment' ).dimmer( 'show' );
		} );
		$hideButton.on( "click", function () {
			$( this ).closest( '.demo' ).find( '.segment' ).dimmer( 'hide' );
		} );
		this.modulesTabContent.find( ".ui.dropdown" ).dropdown();
		this.modulesTabContent.find( ".ui.menu .dropdown" ).dropdown( {on: 'hover'} );

	}
}