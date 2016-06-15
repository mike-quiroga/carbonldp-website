import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import ListViewerComponent from "./../list-viewer/ListViewerComponent";
import PropertyValuecomponent from "./../property-value/PropertyValuecomponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-property",
	template: template,
	directives: [ ListViewerComponent, PropertyValuecomponent ],
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	@Input() documentURI:string;
	@Input() property:RDFNode.Class|RDFNode.Class[];
	@Input() propertyName:string;
	@Output() onGoToBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onGoToNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<Property> = new EventEmitter<Property>();
	commonHeaders:string[] = [ "@id", "@type", "@value" ];

	savingProperty:boolean = false;

	constructor( element:ElementRef ) {
		this.element = element;
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
		let headers:string[] = this.getHeaders( ! ! property ? property : this.property );
		return headers.indexOf( header ) > - 1 ? true : false;
	}

	hasCommonHeaders( property?:any ):boolean {
		let headers:string[] = this.getHeaders( ! ! property ? property : this.property );
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
		return Literal.Factory.is( property );
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

	getJSON( obj:any ):string {
		return JSON.stringify( obj, null, 2 );
	}

	initializeAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	changePropertyValue( value:string, propIndex:number ) {
		this.savingProperty = true;
		if ( this.property.length > 0 ) {
			this.property[ propIndex ][ "@value" ] = value;
		} else {
			this.property[ 0 ] = value;
		}
		let prop:Property = <Property>{};
		prop.name = this.propertyName;
		prop.value = this.property;
		this.onChangeProperty.emit( prop );
	}
}
export interface Property {
	name:string;
	value:any;
}
