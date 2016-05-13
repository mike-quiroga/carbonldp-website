import { Component, ElementRef, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import HighlightDirective from "./../../../../../../directives/HighlightDirective";
import ListViewerComponent from "./../list-viewer/ListViewerComponent"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-property",
	template: template,
	directives: [ CORE_DIRECTIVES, ListViewerComponent, HighlightDirective ],
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	@Input() documentURI:string;
	@Input() property:RDFNode.Class;
	@Input() propertyName:string;
	@Output() onGoTobNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onGoToNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	commonHeaders:string[] = [ "@id", "@type", "@value" ];

	loadingDocument:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeTabs();
		this.initializeAccordions();
	}

	getDisplayName( uri:string ):string {
		if ( this.commonHeaders.indexOf( uri ) > - 1 )
			return uri;
		if ( URI.Util.hasFragment( uri ) )
			return this.getFragment( uri );
		return URI.Util.getSlug( uri );
	}

	hasHeader( header:string, property?:any ):boolean {
		let headers:string[] = this.getHeaders( ! ! property ? property : this.property );
		return headers.indexOf( header ) > - 1 ? true : false;
	}

	hasCommonHeaders( property?:any ):boolean {
		let headers:string[] = this.getHeaders( ! ! property ? property : this.property );
		return headers.indexOf( "@id" ) > - 1 ? true : headers.indexOf( "@type" ) > - 1 ? true : headers.indexOf( "@value" ) > - 1 ? true : false;
	}

	getHeaders( property:any[] ):string[] {
		let temp:string[] = [];
		property.forEach(
			( prop )=> {
				temp = temp.concat( Object.keys( prop ) );
			}
		);
		let headers:string[] = temp.filter(
			( item, pos ) => {
				return temp.indexOf( item ) == pos
			} );
		return headers;
	}

	getFragment( uri:string ):string {
		return URI.Util.getFragment( uri );
	}

	getTypeOf( property:any ):string {
		return typeof property;
	}

	isLiteral( property:any ):boolean {
		return Literal.Factory.is( property );
	}

	isArray( property:any ):boolean {
		return Utils.isArray( property );
	}

	isUrl( uri:string ):boolean {
		let r = /^(ftp|http|https):\/\/[^ "]+$/;
		return r.test( uri );
	}

	isbNode( uri:string ):boolean {
		return ! ! uri ? URI.Util.isBNodeID( uri ) : false;
	}

	isNamedFragment( uri:string ):boolean {
		return ! ! uri ? URI.Util.isFragmentOf( uri, this.documentURI ) : false;
	}

	goTobNode( id:string ):void {
		this.onGoTobNode.emit( id );
	}

	goToNamedFragment( id:string ):void {
		this.onGoToNamedFragment.emit( this.getNormalizedUri( id ) );
	}

	getTypeIcon( type:string ):string {
		switch ( this.getDisplayName( type ) ) {
			case "RDFSource":
				return "file outline";
			case "Container":
				return "cubes";
			case "BasicContainer":
				return "cube";
			default:
				return "file excel outline";
		}
	}

	getJson( obj:any ):string {
		return JSON.stringify( obj, null, 2 );
	}

	getNormalizedUri( uri:string ):string {
		return uri.replace( /[^\w\s]/gi, "" );
	}

	initializeTabs():void {
		this.$element.find( ".tabular.menu .item" ).tab();
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}
}
