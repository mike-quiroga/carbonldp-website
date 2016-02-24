import { Component, Input, ElementRef } from 'angular2/core';
import {CORE_DIRECTIVES} from "angular2/common"
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';
import './style.css!';
import template from './template.html!';

@Component( {
	selector: 'sidebar-component',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )

export default class SideBarComponent {

	static parameters = [ [ ElementRef ] ];

	elementRef:ElementRef;
	$element;
	$container;
	$followMenu;
	sidebar:any;
	sections:any;
	subSections:any;

	@Input() parentelement;
	@Input() mobile;

	host:string = "dev.carbonldp.com";

	constructor( element:ElementRef  ) {
		this.elementRef = element;
		this.$element = $( element.nativeElement );
	}

	ngAfterViewInit():void {
		this.$container = $(this.parentelement.nativeElement).find( "article" );
		this.sections = this.$container.children( "section" );
		this.subSections = this.sections.children( "section" );
		this.$followMenu = this.$element.find( ".following.menu" );
		this.sidebar = this.$element.find( "nav" );
		this.buildSideBar();
	}


	buildSideBar():void {
		if ( this.sidebar ) {
			let html:string = "",
				$sticky:JQuery,
				_self = this
				;
			// Foreach section in the article
			$.each( this.sections, function ( index:number, section:HTMLElement ) {
				let
					$currentSection = $( section ),
					subSections = $currentSection.children( "section" ),
					activeClass = (index === 0) ? 'active ' : '',
					text = _self.getText( $currentSection ),
					safeName = _self.getSafeText( text ),
					id = window.escape( safeName )
					;

				_self.setId( $currentSection );
				html += '<div class="item">';

				if ( subSections.size() === 0 ) {
					html += '<a class="' + activeClass + 'title " href="#' + id + '"><b>' + text + '</b></a>';
				} else {
					html += '<a class="' + activeClass + 'title "><i class="dropdown icon"></i> <b>' + text + '</b></a>';
				}

				// If there are sections inside a section, then iterate each section
				if ( subSections.size() > 0 ) {
					html += '<div class="' + activeClass + 'content menu">';

					$.each( subSections, function ( index2:number, subSection:HTMLElement ){
						let
							$subSection:JQuery = $( subSection ),
							text:string = _self.getText( $subSection ),
							safeName:string = _self.getSafeText( text ),
							id:string = window.escape( safeName )
							;

						_self.setId( $subSection );
						html += '<a class="item" href="#' + id + '">' + text + '</a>';
					});
					html += '</div>';
				}
				html += '</div>';
			});


			if(!this.mobile){
				this.$followMenu = $( '<div />' ).addClass( 'ui vertical following fluid accordion text menu' ).html( html );
				$sticky = $( '<div />' ).addClass( 'ui sticky segment' ).html( this.$followMenu ).prepend( '<p class="ui header">Content</p>' );
				this.sidebar.html( $sticky );
				this.sections.visibility( {
					observeChanges: true,
					once: false,
					offset: 150,
					onTopPassed: function () {
						_self.activateSection( this );
					},
					onTopPassedReverse: function () {
						_self.activatePrevious();
					}
				});
				this.subSections.visibility( {
					observeChanges: true,
					once: false,
					offset: 150,
					onTopPassed: function () {
						_self.activateSubSection( this );
					},
					onTopPassedReverse: function () {
						_self.activateSubSection( this );
					}
				});
			}
			else{
				this.$followMenu = $( '<div />' ).addClass( 'ui fluid vertical following accordion menu mobile' ).html( html );
				$sticky = $( '<div />' ).addClass( 'ui segment' ).html( this.$followMenu ).prepend( '<p class="ui header">Content</p>' );
				this.sidebar.html( $sticky );
			}


			this.sidebar.find( ".ui.sticky" ).sticky( {
				observeChanges: true,
				context: "#article",
				offset: 100
			});

			this.$followMenu.accordion({
				exclusive: false,
				animateChildren: false,
			}).find( '.menu a[href], .title[href]' )
				.on( 'click', this.scrollTo );
		}
	}

	activateSection( elm:any):void{
		var
			$section = $( elm ),
			index = this.sections.index( $section ),
			$followSection = this.$followMenu.children( '.item' ),
			$activeSection = $followSection.eq( index ),
			isActive = $activeSection.hasClass( 'active' )
			;

		if ( ! isActive ){
			$followSection.filter( '.active' ).removeClass( 'active' );
			$activeSection.addClass( 'active' );
			this.$followMenu.accordion( 'open', index );
		}
	}

	// Contracts accordion sections as you scroll through the sections elements
	// inside a section of the page.
	activatePrevious():void {
		var
			$menuItems = this.$followMenu.children( '.item' ),
			$section = $menuItems.filter( '.active' ),
			index = $menuItems.index( $section )
			;

		if ( $section.prev().size() > 0 ) {
			$section.removeClass( 'active' ).prev( '.item' ).addClass( 'active' );
			this.$followMenu.accordion( 'open', index - 1 );
		}
	}
	// Expands accordion sections as you scroll through the sections elements
	// inside a section of the page.
	activateSubSection( elm:any ):void {
		let
			$section = $( elm ),
			index = this.subSections.index( $section ),
			$followSection = this.$followMenu.find( '.menu > .item' ),
			$activeSection = $followSection.eq( index ),
			inClosedTab = ($( this ).closest( '.tab:not(.active)' ).size() > 0),
			anotherSection = ($( this ).filter( 'section' ).size() > 0),
			isActive = $activeSection.hasClass( 'active' )
			;

		if ( index !== - 1 && ! inClosedTab && ! anotherSection && ! isActive ) {
			$followSection.filter( '.active' ).removeClass( 'active' );
			$activeSection.addClass( 'active' );
		}
	}

	scrollTo( event:any ):boolean {
		let
			id:string = $( event.currentTarget ).attr( 'href' ).replace( '#', '' ),
			$element:JQuery = $( '#' + id ),
			position:number = $element.offset().top - 100
			;

		$element.addClass( 'active' );
		$( 'html, body' ).animate({
			scrollTop: position
		}, 500 );
		location.hash = '#' + id;
		event.stopImmediatePropagation();
		event.preventDefault();
		return false;
	}

	// Gets the name of the section using the first children header of the section
	getText( $section:JQuery ):string {
		if ( $section.children( ":header" ).eq( 0 ).text() )
			return $section.children( ":header" ).eq( 0 ).text();
		return "";
	}

	// Escapes a given text to use it safely with selectors
	getSafeText( text:string ):string {
		return text.replace( /[,]/g, '' ).replace( /\s+/g, '-' ).replace( /[^-,'A-Za-z0-9]+/g, '' ).toLowerCase();
	}

	// Sets the id to the section using the name of the first children header of the section
	setId( $section:JQuery ):void {
		if ( $section.children( ":header" ).eq( 0 ).text() ){
			let text = this.getText( $section ),
				safeName = this.getSafeText( text ),
				id = window.escape( safeName )
				;
			$section.attr( 'id', id );
		}
	}
}




