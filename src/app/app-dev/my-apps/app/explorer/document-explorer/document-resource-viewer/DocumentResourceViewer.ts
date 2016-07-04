import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { Property, PropertyRow, Modes } from "./../property/PropertyComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ PropertyComponent ],
} )

export default class DocumentResourceComponent {

	element:ElementRef;
	$element:JQuery;
	modes:Modes = Modes;
	@Input() displayOnly:string[] = [];
	@Input() hiddenProperties:string[] = [];
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;
	@Input() documentURI:string = "";

	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	@Output() onDeleteProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	@Output() onAddProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	@Output() onSaveNewProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	properties:PropertyRow[] = [];

	_rootNode:RDFNode.Class;
	@Input() set rootNode( value:RDFNode.Class ) {
		this._rootNode = value;
		this.getProperties();
	}

	get rootNode() {
		return this._rootNode;
	}

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	openBNode( id:string ):void {
		this.onOpenBNode.emit( id );
	}

	openNamedFragment( id:string ):void {
		this.onOpenNamedFragment.emit( id );
	}

	canDisplay( propertyName:any ):boolean {
		if ( typeof propertyName === "undefined" ) return false;
		if ( this.displayOnly.length === 0 && this.hiddenProperties.length === 0 ) return true;
		if ( this.displayOnly.length > 0 ) return this.displayOnly.indexOf( propertyName ) !== - 1 ? true : false;
		return this.hiddenProperties.indexOf( propertyName ) !== - 1 ? false : true;
	}

	changeProperty( property:Property, propertyRow:PropertyRow ):void {
		this.onChangeProperty.emit( propertyRow );
	}

	deleteProperty( property:Property, propertyRow:PropertyRow ):void {
		this.onDeleteProperty.emit( propertyRow );
	}

	deleteNewProperty( property:Property, propertyRow:PropertyRow, index:number ):void {
		this.properties.splice( index, 1 );
		this.onDeleteProperty.emit( propertyRow );
	}

	saveNewProperty( property:Property, propertyRow:PropertyRow ):void {
		this.onSaveNewProperty.emit( propertyRow );
	}

	addProperty( property:Property, propertyRow:PropertyRow ):void {
		let newProperty:PropertyRow = <PropertyRow>{
			added: <Property>{
				id: "",
				name: "New Property",
				value: []
			}
		};
		this.properties.splice( 2, 0, newProperty );
		if ( ! ! this.$element ) setTimeout( ()=>this.$element.find( "document-property.added-property" ).first().transition( "drop" ) );
	}

	getProperties():void {
		this.properties = [];
		Object.keys( this.rootNode ).forEach( ( propName:string )=> {
			this.properties.push( <PropertyRow>{
				copy: <Property>{
					id: propName,
					name: propName,
					value: this.rootNode[ propName ]
				}
			} );
		} );
	}
}
