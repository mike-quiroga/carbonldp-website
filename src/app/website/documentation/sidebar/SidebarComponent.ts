import { Component, Input, ElementRef, OnChanges, SimpleChange } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common"
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from "@angular/router-deprecated";
import SidebarService from "./service/SidebarService";

import $ from "jquery";
import "semantic-ui/semantic";
import "./style.css!";
import template from "./template.html!";

@Component( {
	selector: "sidebar-component",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class SidebarComponent {
	elementRef:ElementRef;
	$element:JQuery;
	$container:JQuery;
	$followMenu:JQuery;
	sidebar:any;
	sections:any;
	subSections:any;

	@Input() parentElement:ElementRef;
	@Input() mobile:boolean;
	@Input() contentReady:boolean;

	host:string = "dev.carbonldp.com";

	constructor( element:ElementRef ) {
		this.elementRef = element;
		this.$element = $( element.nativeElement );
	}

	ngOnChanges( changeRecord:any ):void {
		if ( "contentReady" in changeRecord ) {
			let change:SimpleChange = changeRecord.contentReady;
			if ( change.currentValue ) this.buildSidebar();
		}
	}

	ngAfterViewInit():void {
	this.$container = $( this.parentElement.nativeElement ).find( "article" );
	this.sections = this.$container.children( "section" );
	this.subSections = this.sections.children( "section" );
		this.$followMenu = this.$element.find( ".following.menu" );
		this.sidebar = this.$element.find( "nav" );
		this.createAccordions();
	}

	createAccordions():void {
		this.$followMenu.accordion();
	}

	buildSidebar():void {
		if ( typeof this.sidebar === "undefined" || this.sidebar.length === 0 ) return;

		let html:string = "";
		let $sticky:JQuery;
		let _self = this;

		// Foreach section in the article build template
		$.each( this.sections, function ( index:number, section:HTMLElement ) {
			let $currentSection:JQuery = $( section );
			html += _self.createMenuSectionItemHTML( $currentSection, index === 0 );
		} );

		// Assign class for computer and tablets size sidebar
		if ( ! this.mobile ) {
			this.$followMenu = $( "<div />" ).addClass( "ui vertical following fluid accordion text menu" ).html( html );
			$sticky = $( "<div />" ).addClass( "ui sticky segment" ).html( this.$followMenu ).prepend( '<p class="ui header">Content</p>' );
			this.sidebar.html( $sticky );
			this.sections.visibility( {
				once: false,
				offset: 150,
				onTopPassed: function () {
					_self.activateSection( this );
				},
				onBottomPassedReverse: function () {
					_self.activateSection( this );
				},
			} );
			this.subSections.visibility( {
				once: false,
				offset: 150,
				onTopPassed: function () {
					_self.activateSubSection( this );
				},
				onBottomPassedReverse: function () {
					_self.activateSubSection( this );
				}
			} );
		} else {
			// Assing classes for mobile size sidebar
			this.$followMenu = $( "<div />" ).addClass( "ui vertical following fluid accordion menu mobile" ).html( html );
			$sticky = $( "<div />" ).addClass( "ui segment" ).html( this.$followMenu ).prepend( '<p class="ui header">Content</p>' );
			this.sidebar.html( $sticky );
		}

		this.sidebar.find( ".ui.sticky" ).sticky( {
			observeChanges: true,
			context: "#article",
			offset: 100
		} );

		this.$followMenu.find( ".menu a[href], .title[href]" ).on( "click", this.scrollTo );
		this.$followMenu.find( ".item > .dropdown.icon" ).on( "click", this.toggleDropdown );

	}

	// Build template for each section of the article
	createMenuSectionItemHTML( $section, active ):string {
		let subSections:JQuery = $section.children( "section" );
		let activeClass:string = active ? "active" : "";
		let headerText:string = this.getHeaderText( $section );
		let headerID:string = this.getHeaderID( headerText );

		this.setSectionID( $section, headerID );
		let html:string = `<div class="item">`;

		if ( subSections.length === 0 ) {
			html += `<a class="${ activeClass } title " href="#${ headerID }">${ headerText }</a></div>`;
		} else {
			html += `<a class="${ activeClass } title " href="#${ headerID }">${ headerText }</a><i class="dropdown icon"></i>`;
		}

		// If subsections exist, then iterate each section
		let component:SidebarComponent = this;
		if ( subSections.length > 0 ) {
			html += `<div class="content menu">`;

			$.each( subSections, function ( index:number, subSection:HTMLElement ) {
				let $subSection:JQuery = $( subSection );
				html += component.createMenuSubsectionItemHTML( $subSection );
			} );
			html += `</div>`;
		}
		html += `</div>`;

		return html;
	}

	// Build template for each subsection of the section
	createMenuSubsectionItemHTML( $subSection:JQuery ):string {
		let headerText:string = this.getHeaderText( $subSection );
		let headerID:string = this.getHeaderID( headerText );

		this.setSectionID( $subSection, headerID );
		return `<a class="item" href="#${ headerID }">${ headerText }</a>`;
	}

	activateSection( elm:any ):void {
		let $section:JQuery = $( elm );
		let index:number = this.sections.index( $section );
		let $followSection:JQuery = this.$followMenu.children( ".item" );
		let $currentSection:JQuery = $followSection.eq( index );
		let isActive:boolean = $currentSection.hasClass( "active" );
		let hasSubsection:boolean = this.sections.eq( index ).children( "section" ).length > 0;

		$followSection.removeClass( "active" );
		$followSection.find( ".active" ).not( ".toggled" ).removeClass( "active" );

		if ( ! isActive ) {

			$currentSection.addClass( "active" );
		}

		if ( hasSubsection ) {
			$currentSection.find( ".menu" ).addClass( "active" );
		}

		$( ".ui.sticky" ).sticky( "refresh" );

	}

	// Expands accordion sections as you scroll through the sections elements inside a section of the page.
	activateSubSection( elm:any ):void {
		let $section:JQuery = $( elm );
		let index:number = this.subSections.index( $section );
		let $followSection:JQuery = this.$followMenu.find( ".menu > .item" );
		let $activeSection:JQuery = $followSection.eq( index );
		let isActive:boolean = $activeSection.hasClass( "active" );
		let $accordion:JQuery = this.$followMenu.children( ".item" ).find( ".menu" );
		let accordionIsActive:boolean = $accordion.hasClass( "active" );

		if ( index !== - 1 && ! isActive ) {
			$followSection.filter( ".active" ).removeClass( "active" );
			$activeSection.addClass( "active" );
		}

		if ( ! accordionIsActive ) {
			$accordion.addClass( "active" );
		}

	}

	// Scroll to selected section or subsection in the article
	scrollTo( event:any ):boolean {
		let id:string = $( event.currentTarget ).attr( "href" ).replace( "#", "" );
		let $element:JQuery = $( "#" + id );
		let position:number = $element.offset().top - 100;

		$element.addClass( "active" );

		$( "html, body" ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = "#" + id;
		event.stopImmediatePropagation();
		event.preventDefault();

		return false;
	}

	// Toggle selected accordion menu in sidebar
	toggleDropdown( event:any ):boolean {
		let $target:JQuery = $( event.currentTarget );
		let $accordion:JQuery = $target.parent( ".item" ).find( ".content.menu" );
		if ( $accordion ) {
			let accordionIsActive:boolean = $accordion.hasClass( "active" );

			if ( accordionIsActive ) {
				$accordion.removeClass( "active" );
				$accordion.removeClass( "toggled" );
			} else {
				$accordion.addClass( "active" );
				$accordion.addClass( "toggled" );
			}
		}
		$( ".ui.sticky" ).sticky( "refresh" );

		return false;
	}

	// Gets the name of the section using the first children header of the section
	getHeaderText( $section:JQuery ):string {
		return $section.children( ":header" ).eq( 0 ).text();
	}

	// Escapes a given text to use it safely with selectors
	getHeaderID( text:string ):string {
		text = text.replace( /[,]/g, "" ).replace( /\s+/g, "-" ).replace( /[^-,'A-Za-z0-9]+/g, "" ).toLowerCase();
		return encodeURIComponent( text );
	}

	// Sets the id to the section using the name of the first children header of the section
	setSectionID( $section:JQuery, headerID:string ):void {
		$section.attr( "id", headerID );
	}
}
