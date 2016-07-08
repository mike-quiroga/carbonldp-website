import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/property.component";
import { Property, PropertyRow, Modes } from "./../property/property.component";

import template from "./document-resource.component.html!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ PropertyComponent ],
} )

export default class DocumentResourceComponent {

	element:ElementRef;
	$element:JQuery;
	modes:Modes = Modes;
	properties:PropertyRow[] = [];
	existingProperties:string[] = [];
	records:RootRecords;
	private _rootHasChanged:boolean;
	set rootHasChanged( hasChanged:boolean ) {
		this._rootHasChanged = hasChanged;
		this.onChanges.emit( this.records );
	}

	get rootHasChanged() {
		return this._rootHasChanged;
	}

	@Input() displayOnly:string[] = [];
	@Input() hiddenProperties:string[] = [];
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;
	@Input() documentURI:string = "";
	private _rootNode:RDFNode.Class;
	@Input() set rootNode( value:RDFNode.Class ) {
		this._rootNode = value;
		this.records = new RootRecords();
		this.getProperties();
	}

	get rootNode() {
		return this._rootNode;
	}

	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChanges:EventEmitter<RootRecords> = new EventEmitter<RootRecords>();


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

	changeProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new RootRecords();
		if ( typeof property.modified !== "undefined" ) {
			this.records.changes.set( property.modified.id, property );
		} else {
			this.records.changes.delete( property.copy.id );
		}
		this.updateExistingProperties();
		this.rootHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	deleteProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new RootRecords();
		if ( typeof property.added !== "undefined" ) {
			this.records.additions.delete( property.added.id );
			this.properties.splice( index, 1 );
		} else if ( typeof property.deleted !== "undefined" ) {
			this.records.deletions.set( property.deleted.id, property );
		}
		this.updateExistingProperties();
		this.rootHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	addProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new RootRecords();
		if ( typeof property.added !== "undefined" ) {
			if ( property.added.id === property.added.name ) {
				this.records.additions.set( property.added.id, property );
			} else {
				this.records.additions.delete( property.added.id );
				this.records.additions.set( property.added.name, property );
			}
		}
		this.updateExistingProperties();
		this.rootHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	createProperty( property:Property, propertyRow:PropertyRow ):void {
		let newProperty:PropertyRow = <PropertyRow>{
			added: <Property>{
				id: "",
				name: "New Property",
				value: []
			}
		};
		this.properties.splice( 2, 0, newProperty );
		if ( ! ! this.$element ) setTimeout( ()=>this.$element.find( "property.added-property" ).first().transition( "drop" ) );
	}

	getProperties():void {
		this.properties = [];
		this.updateExistingProperties();
		this.existingProperties.forEach( ( propName:string )=> {
			this.properties.push( {
				copy: {
					id: propName,
					name: propName,
					value: this.rootNode[ propName ]
				}
			} );
		} );
	}

	updateExistingProperties():void {
		this.existingProperties = Object.keys( this.rootNode );
		if ( ! this.records ) return;
		this.records.additions.forEach( ( value, key )=> {
			this.existingProperties.push( key );
		} );
		this.records.changes.forEach( ( value, key )=> {
			if ( value.modified.id !== value.modified.name ) {
				this.existingProperties.splice( this.existingProperties.indexOf( value.modified.id ), 1, value.modified.name );
			}
		} );
		this.records.deletions.forEach( ( value, key )=> {
			this.existingProperties.splice( this.existingProperties.indexOf( value.deleted.id ), 1 );
		} );
	}
}

export class RootRecords {
	changes:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	deletions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	additions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
}
