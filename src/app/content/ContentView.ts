import { Component, CORE_DIRECTIVES, DynamicComponentLoader, ElementRef, View} from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';
import ContentService from 'app/content/ContentService';

import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import ComponentRef = ng.ComponentRef;

import "./style.css!";

@Component( {
	// Selector matches the route alias?
	selector: 'compiled-content',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]
} )
export default class ContentView {

	static parameters = [ [ Router ], [ ContentService ], [ RouteParams ], [ DynamicComponentLoader ], [ ElementRef ] ];

	router:Router;
	contentService:ContentService;
	routeParams:RouteParams;
	dynamicComponentLoader:DynamicComponentLoader;
	elementRef:ElementRef;
	$element;
	compiledComponent;

	constructor( router:Router, contentService:ContentService, routeParams:RouteParams, dynamicComponentLoader:DynamicComponentLoader, elementRef:ElementRef ) {

		console.log( ">> ContentView -> constructed" );

		this.router = router;
		this.contentService = contentService;
		this.routeParams = routeParams;
		this.dynamicComponentLoader = dynamicComponentLoader;
		this.elementRef = elementRef;
		this.compiledComponent = CompiledComponent;

		let id = this.routeParams.get( 'id' );

		// console.log("-- ContentView -> Got id: " + id);


		// START: OPTION A -------------------------------------------------------------------------
		// USE THIS TO LOAD A TEMPLATE DIRECTLY FROM TEMPLATE URL
		@Component( {
			selector: 'compiled-component',
			directives: [ CodeMirrorComponent.Class ],
			//templateUrl: 'http://127.0.0.1:8080/assets/documents/' + id + '.html'
			templateUrl: '/assets/documents/' + id
		} )
		class CompiledComponent {

			static parameters = [ [ ElementRef ] ];

			elementRef:ElementRef;
			$element;

			$container;
			$followMenu;

			sidebar:any;
			sections:any;
			subSections:any;

			host:string = "dev.carbonldp.com";

			constructor( elementRef:ElementRef ) {
				this.elementRef = elementRef;
				this.$element = $( this.elementRef.nativeElement );


			}

			afterViewInit():void {
				this.createAccordions();
				this.evalJavascript();

				this.$container = $( "article" );
				this.sections = this.$container.children( "section" );
				this.subSections = this.sections.children( "section" );
				this.$followMenu = this.$element.find( ".following.menu" );
				this.sidebar = this.$element.find( "nav" );
				this.buildSideBar();
			}

			createAccordions():void {
				this.$element.find( '.ui.accordion' ).accordion();
			}

			// Enables the use of inline JavaScript by placing script in hidden DIV elements with class
			// "script".
			// <div class="script">...</div>
			// Yes, we know that Angular frowns upon this. It shouldn't be used for the wrong things.
			// But there ARE rare cases where this is handy.
			evalJavascript():void {
				let scripts:any[] = this.elementRef.nativeElement.querySelectorAll( ".script" );
				let i:number = 0, scriptLength = scripts.length;
				for ( i; i < scriptLength; i ++ ) {
					eval( scripts[ i ].textContent );
				}
			}

			// Expands accordion sections as you scroll through the sections elements
			// inside a section of the page.
			activateSection( elm:any ):void {
				//console.log( ">> activateSection()" );
				var
					$section = $( elm ),
					index = this.sections.index( $section ),
					$followSection = this.$followMenu.children( '.item' ),
					$activeSection = $followSection.eq( index ),
					isActive = $activeSection.hasClass( 'active' )
					;
				if ( ! isActive ) {
					$followSection.filter( '.active' ).removeClass( 'active' );
					$activeSection.addClass( 'active' );
					this.$followMenu.accordion( 'open', index );
				}
			}

			// Contracts accordion sections as you scroll through the sections elements
			// inside a section of the page.
			activatePrevious():void {
				//console.log( ">> activatePrevious()" );
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
				//console.log( ">> activateSubSection()" );
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


			buildSideBar():void {
				if ( this.sidebar ) {
					let html:string = "",
						$sticky:JQuery,
						_self = this;

					// Foreach section in the article
					$.each( this.sections, function ( index:number, section:HTMLElement ) {
						let
							$currentSection = $( section ),
							subSections = $currentSection.children( "section" ),
							activeClass = (index === 0) ? 'active ' : '',
							text = _self.getText( $currentSection ),
							safeName = _self.getSafeText( text ),
							id = window.escape( safeName );
						_self.setId( $currentSection );
						html += '<div class="item">';
						if ( subSections.size() === 0 ) {
							html += '<a class="' + activeClass + 'title " href="#' + id + '"><b>' + text + '</b></a>';
						}
						else {
							html += '<a class="' + activeClass + 'title "><i class="dropdown icon"></i> <b>' + text + '</b></a>';
						}
						// If there are sections inside a section, then iterate each section
						if ( subSections.size() > 0 ) {
							html += '<div class="' + activeClass + 'content menu">';
							$.each( subSections, function ( index2:number, subSection:HTMLElement ) {
								let
									$subSection:JQuery = $( subSection ),
									text:string = _self.getText( $subSection ),
									safeName:string = _self.getSafeText( text ),
									id:string = window.escape( safeName );
								_self.setId( $subSection );
								html += '<a class="item" href="#' + id + '">' + text + '</a>';
							} );
							html += '</div>';
						}
						html += '</div>';
					} );
					this.$followMenu = $( '<div />' ).addClass( 'ui vertical following fluid accordion text menu' ).html( html );
					$sticky = $( '<div />' ).addClass( 'ui fixed sticky segment' ).html( this.$followMenu ).prepend( '<h4 class="ui header">Content</h4>' );
					this.sidebar.html( $sticky );
					this.sidebar.find( ".ui.sticky" ).sticky( {
						context: '#article',
						offset: 100
					} );
					this.sidebar.find( ".ui.sticky" ).sticky( "refresh" );
					this.$followMenu.accordion( {
							exclusive: false,
							animateChildren: false,
							onChange: function () {
								_self.$element.find( ".ui.sticky" ).sticky( 'refresh' );
							}
						} )
						.find( '.menu a[href], .title[href]' )
						.on( 'click', this.scrollTo );
					this.sections.visibility( {
						observeChanges: false,
						once: false,
						offset: 150,
						onTopPassed: function () {
							_self.activateSection( this );
						},
						onTopPassedReverse: function () {
							_self.activatePrevious();
						}
					} );
					this.subSections.visibility( {
						observeChanges: false,
						once: false,
						offset: 150,
						onTopPassed: function () {
							_self.activateSubSection( this );
						},
						onTopPassedReverse: function () {
							_self.activateSubSection( this );
						}
					} );
				}
			}

			// Gets the name of the section using the first children header of the section
			getText( $section:JQuery ):string {
				if ( $section.children( ":header" ).eq( 0 ).text() )
					return $section.children( ":header" ).eq( 0 ).text();
				return "";
			}

			// Escapes a given text to use it safely with selectors
			getSafeText( text:string ):string {
				return text.replace( /\s+/g, '-' ).replace( /[^-,'A-Za-z0-9]+/g, '' ).toLowerCase();
			}

			// Sets the id to the section using the name of the first children header of the section
			setId( $section:JQuery ):void {
				if ( $section.children( ":header" ).eq( 0 ).text() ) {
					let text = this.getText( $section ),
						safeName = this.getSafeText( text ),
						id = window.escape( safeName );
					$section.attr( 'id', id );
				}
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


		dynamicComponentLoader.loadIntoLocation( CompiledComponent, elementRef, 'container' );

		// END: OPTION A ---------------------------------------------------------------------------


		// START: OPTION B: ------------------------------------------------------------------------
		// USE THIS TO LOAD A TEMPLATE DYNAMICALLY FROM HTTP USING A SERVICE

		/*
		this.contentService.getDocumentById( id ).then(
			( content )=> {
				@Component({
					selector: 'compiled-component',
					directives: [CodeMirrorComponent.Class],
					template: content
				})
				class CompiledComponent {

					//testProperty:string = "component in scope";

				};

				dynamicComponentLoader.loadIntoLocation(CompiledComponent, elementRef, 'container');
			}
		);
		*/

		// END: OPTION B --------------------------------------------------------------------------

	}

}

