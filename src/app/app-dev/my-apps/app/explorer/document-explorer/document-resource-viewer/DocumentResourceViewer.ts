import { Component, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { Property } from "./../property/PropertyComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ PropertyComponent ],
} )

export default class DocumentResourceComponent {

	@Input() displayOnly:string[] = [];
	@Input() hiddenProperties:string[] = [];
	@Input() documentChanges:Map<string,Property>;
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<Property> = new EventEmitter<Property>();
	properties:Property[] = [];

	_rootNode:RDFNode.Class;
	@Input() set rootNode( value:RDFNode.Class ) {
		this._rootNode = value;
		this.getProperties();
	}

	get rootNode() {
		return this._rootNode;
	}

	constructor() {}


	openBNode( id:string ):void {
		this.onOpenBNode.emit( id );
	}

	openNamedFragment( id:string ):void {
		this.onOpenNamedFragment.emit( id );
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	canDisplay( propertyName:string ):boolean {
		if ( typeof propertyName === "undefined" ) return false;
		if ( this.displayOnly.length === 0 && this.hiddenProperties.length === 0 ) return true;
		if ( this.displayOnly.length > 0 ) return this.displayOnly.indexOf( propertyName ) !== - 1 ? true : false;
		return this.hiddenProperties.indexOf( propertyName ) !== - 1 ? false : true;
	}

	changeProperty( property:Property ):void {
		this.onChangeProperty.emit( property );
	}

	propertyHasChanged( propertyId:string ):boolean {
		return this.documentChanges.has( propertyId );
	}

	getProperties():void {
		this.properties = [];
		Object.keys( this.rootNode ).forEach( ( propName:string )=> {
			this.properties.push( <Property>{
				id: propName,
				name: propName,
				value: JSON.parse( JSON.stringify( this.rootNode[ propName ] ) )
			} );
		} );
	}
}
