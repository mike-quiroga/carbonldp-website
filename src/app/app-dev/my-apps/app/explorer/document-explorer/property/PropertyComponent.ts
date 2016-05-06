import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as RDFValue from "carbonldp/RDF/Value";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import HighlightDirective from "./../../../../../../directives/HighlightDirective";

import template from "./template.html!";
// import "./style.css!";

@Component( {
	selector: "document-property",
	template: template,
	directives: [ CORE_DIRECTIVES, HighlightDirective ],
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	@Input() property:RDFNode.Class;
	@Input() propertyName:string;
	@Output() onGoTobNode:EventEmitter<string> = new EventEmitter<string>();


	loadingDocument:boolean = false;
	inspectingDocument:RDFDocument.Class;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		console.log( "Property: %o", this.property );
		this.$element = $( this.element.nativeElement );
		this.initializeTabs();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "property" ].currentValue !== changes[ "property" ].previousValue ) {
			// console.log( this.property );
			// let documents:RDFNode.Class[] = RDFDocument.Util.getDocumentResources( this.property );
			// this.rootNode = documents[ 0 ];
			// console.log( this.rootNode );
			this.initializeTabs();
		}
	}

	getDisplayName( uri:string ):string {
		if ( uri === "@id" || uri === "@type" )
			return uri;
		if ( URI.Util.hasFragment( uri ) )
			return this.getFragment( uri );
		return URI.Util.getSlug( uri );
	}

	getProperties( document:RDFNode.Class ):RDFValue.Class[] {
		let properties:RDFValue.Class[] = [];
		for ( let property in document ) {
			if ( ! document.hasOwnProperty( property ) ) continue;
			let prop:Object = {};
			prop[ property ] = document[ property ];
			properties.push( prop );
		}
		return properties;
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

	goTobNode( id:string ):void {
		this.onGoTobNode.emit( id );
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

	initializeTabs():void {
		if ( ! this.$element )
			this.$element = $( this.element.nativeElement );
		this.$element.find( ".tabular.menu .item" ).tab();
	}
}
