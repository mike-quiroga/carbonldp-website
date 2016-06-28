import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as SDKRDFNode from "carbonldp/RDF/RDFNode";
import * as SDKLiteral from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import ListViewerComponent from "./../list-viewer/ListViewerComponent";
import LiteralsComponent from "./literals/LiteralsComponent";
import { Literal, LiteralRow } from "./literals/literal/LiteralComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-property",
	template: template,
	directives: [ ListViewerComponent, LiteralsComponent ],
	host: { "[class.has-changed]": "propertyHasChanged" },
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	literals:LiteralRow[];
	pointers:SDKRDFNode.Class[];
	tempProperty:Property = <Property>{};
	id:string;
	name:string;
	value:any;
	@Input() documentURI:string;
	@Output() onGoToBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onGoToNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<Property> = new EventEmitter<Property>();
	commonHeaders:string[] = [ "@id", "@type", "@value" ];
	_property:PropertyRow;
	@Input() set property( prop:PropertyRow ) {
		this._property = prop;
		this.id = prop.copy.id;
		this.name = prop.copy.name;
		if ( Utils.isArray( prop.copy.value ) ) {
			this.value = [];
			prop.copy.value.forEach( ( literalOrRDFNode )=> { this.value.push( Object.assign( literalOrRDFNode ) ) } )
		} else {
			this.value = prop.copy.value;
		}
	}

	get property():PropertyRow { return this._property; }

	_propertyHasChanged:boolean;
	set propertyHasChanged( value:boolean ) {
		this._propertyHasChanged = value;
	}

	get propertyHasChanged():boolean { return this._propertyHasChanged; }


	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		if ( Utils.isArray( this.value ) ) this.fillLiteralsAndRDFNodes();
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
	}

	getDisplayName( uri:string ):string {
		if ( this.commonHeaders.indexOf( uri ) > - 1 )return uri;
		if ( URI.Util.hasFragment( uri ) )return this.getFragment( uri );

		return URI.Util.getSlug( uri );
	}

	getParentURI( uri:string ):string {
		let slug:string = this.getSlug( uri );
		return uri.substr( 0, uri.indexOf( slug ) );
	}

	getSlug( uri:string ) {
		return URI.Util.getSlug( uri );
	}

	hasHeader( header:string, property?:any ):boolean {
		let headers:string[] = this.getHeaders( ! ! property ? property : this.value );
		return headers.indexOf( header ) > - 1 ? true : false;
	}

	hasCommonHeaders( property?:any ):boolean {
		let headers:string[] = this.getHeaders( ! ! property ? property.value : this.value );
		return headers.indexOf( "@id" ) > - 1 ? true : headers.indexOf( "@type" ) > - 1 ? true : headers.indexOf( "@value" ) > - 1 ? true : false;
	}

	getHeaders( property:any[] ):string[] {
		let temp:string[] = [];
		property.forEach( ( prop )=> {
			temp = temp.concat( Object.keys( prop ) );
		} );
		return temp.filter( ( item, pos ) => {
			return temp.indexOf( item ) == pos
		} );
	}

	getFragment( uri:string ):string {
		return URI.Util.getFragment( uri );
	}

	getTypeOf( property:any ):string {
		return typeof property;
	}

	isLiteral( property:any ):boolean {
		return SDKLiteral.Factory.is( property );
	}

	isArray( property:any ):boolean {
		return Utils.isArray( property );
	}

	isUrl( uri:string ):boolean {
		let r = /^(ftp|http|https):\/\/[^ "]+$/;
		return r.test( uri );
	}

	isBNode( uri:string ):boolean {
		return ! ! uri ? URI.Util.isBNodeID( uri ) : false;
	}

	isNamedFragment( uri:string ):boolean {
		return ! ! uri ? URI.Util.isFragmentOf( uri, this.documentURI ) : false;
	}

	goToBNode( id:string ):void {
		this.onGoToBNode.emit( id );
	}

	goToNamedFragment( id:string ):void {
		this.onGoToNamedFragment.emit( id );
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

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	fillLiteralsAndRDFNodes():void {
		this.literals = [];
		this.pointers = [];
		this.property.copy.value.forEach( ( literalOrRDFNode )=> {
			if ( SDKLiteral.Factory.is( literalOrRDFNode ) ) this.literals.push( <LiteralRow>{ copy: literalOrRDFNode } );
			if ( SDKRDFNode.Factory.is( literalOrRDFNode ) ) this.pointers.push( literalOrRDFNode );
		} );
		console.log( this.literals );
	}

	checkForChangesOnLiterals( literals:LiteralRow[] ):void {
		this.changePropertyValues( literals );
		this.propertyHasChanged = ! ! literals.find( ( literalRow )=> {return ! ! literalRow.modified || ! ! literalRow.added } );
		if ( ! this.propertyHasChanged ) delete this.property.modified;
	}

	changePropertyValues( literals:LiteralRow[] ):void {
		this.tempProperty.id = this.id;
		this.tempProperty.name = this.name;
		if ( Utils.isArray( this.value ) ) {
			this.tempProperty.value = [];
			literals.forEach( ( literalRow )=> {
				this.tempProperty.value.push( ! ! literalRow.added ? literalRow.added : ! ! literalRow.modified ? literalRow.modified : literalRow.copy );
			} );
		} else {
			this.tempProperty.value = this.value;
		}
		this.property.modified = this.tempProperty;
		console.log( this.tempProperty );
		this.onChangeProperty.emit( this.tempProperty );
	}


	// changePropertyValue( value:string, propIndex:number ) {
	// 	let property:Property = <Property>JSON.parse( JSON.stringify( this.property ) );
	// 	if ( property.value.length > 0 ) {
	// 		property.value[ propIndex ][ "@value" ] = value;
	// 	} else {
	// 		property.value[ 0 ] = value;
	// 	}
	// 	this.onChangeProperty.emit( property );
	// 	this.property = property;
	// }
}

export interface PropertyRow {
	copy:any;
	modified?:any;
}

export interface Property {
	id:string;
	name:string;
	value:any[];
}
