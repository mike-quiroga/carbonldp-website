import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as SDKRDFNode from "carbonldp/RDF/RDFNode";
import * as SDKLiteral from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Utils from "carbonldp/Utils";

import ListViewerComponent from "./../list-viewer/ListViewerComponent";
import LiteralsComponent from "./literals/LiteralsComponent";
import { LiteralRow } from "./literals/literal/LiteralComponent";
import PointersComponent from "./pointers/PointersComponent";
import { PointerRow } from "./pointers/pointer/PointerComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-property",
	template: template,
	directives: [ ListViewerComponent, LiteralsComponent, PointersComponent ],
	host: { "[class.has-changed]": "propertyHasChanged" },
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	literals:LiteralRow[];
	pointers:PointerRow[];
	tempLiterals:LiteralRow[];
	tempPointers:PointerRow[];
	tempProperty:Property = <Property>{};
	id:string;
	name:string;
	value:any;
	@Input() documentURI:string;
	@Input() bNodes:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	@Input() namedFragments:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
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
		this.tempLiterals = [];
		this.pointers = [];
		this.tempPointers = [];
		this.property.copy.value.forEach( ( literalOrRDFNode )=> {
			if ( SDKLiteral.Factory.is( literalOrRDFNode ) ) {
				this.literals.push( <LiteralRow>{ copy: literalOrRDFNode } );
				this.tempLiterals.push( <LiteralRow>{ copy: literalOrRDFNode } );
			}
			if ( SDKRDFNode.Factory.is( literalOrRDFNode ) ) {
				this.pointers.push( <PointerRow>{ copy: literalOrRDFNode } );
				this.tempPointers.push( <PointerRow>{ copy: literalOrRDFNode } );
			}
		} );
	}

	checkForChangesOnLiterals( literals:LiteralRow[] ):void {
		this.tempLiterals = literals;
		this.changePropertyValues( this.tempPointers.concat( this.tempLiterals ) );
		this.propertyHasChanged = ! ! literals.find( ( literalRow )=> {return ! ! literalRow.modified || ! ! literalRow.added || ! ! literalRow.deleted } );
		if ( ! this.propertyHasChanged ) delete this.property.modified;
	}

	checkForChangesOnPointers( pointers:PointerRow[] ):void {
		this.tempPointers = pointers;
		this.changePropertyValues( this.tempPointers.concat( this.tempLiterals ) );
		this.propertyHasChanged = ! ! pointers.find( ( pointerRow )=> {return ! ! pointerRow.modified || ! ! pointerRow.added || ! ! pointerRow.deleted } );
		if ( ! this.propertyHasChanged ) delete this.property.modified;
	}

	changePropertyValues( literalsOrPointers:LiteralRow[]|PointerRow[] ):void {
		this.tempProperty.id = this.id;
		this.tempProperty.name = this.name;
		if ( Utils.isArray( this.value ) ) {
			this.tempProperty.value = [];
			literalsOrPointers.forEach( ( literalOrPointerRow )=> {
				if ( ! literalOrPointerRow.deleted )this.tempProperty.value.push( ! ! literalOrPointerRow.added ? literalOrPointerRow.added : ! ! literalOrPointerRow.modified ? literalOrPointerRow.modified : literalOrPointerRow.copy );
			} );
		} else {
			this.tempProperty.value = this.value;
		}
		this.property.modified = this.tempProperty;
		console.log( this.tempProperty );
		this.onChangeProperty.emit( this.tempProperty );
	}
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
