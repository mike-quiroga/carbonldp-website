import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { Property, PropertyRow, Modes } from "./../../property/PropertyComponent";
import PropertyComponent from "./../../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "named-fragment",
	template: template,
	directives: [ PropertyComponent ],
} )

export default class NamedFragmentComponent {

	element:ElementRef;
	$element:JQuery;
	modes:Modes = Modes;
	properties:PropertyRow[] = [];
	records:NamedFragmentRecords;
	private _namedFragmentChanged:boolean;
	set namedFragmentChanged( hasChanged:boolean ) {
		this._namedFragmentChanged = hasChanged;
		this.onChanges.emit( this.records );
	}

	get namedFragmentChanged() {
		return this.namedFragmentChanged;
	}

	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;
	@Input() documentURI:string = "";
	private _namedFragment:RDFNode.Class;
	@Input() set namedFragment( value:RDFNode.Class ) {
		this._namedFragment = value;
		this.getProperties();
	}

	get namedFragment() {
		return this._namedFragment;
	}

	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChanges:EventEmitter<NamedFragmentRecords> = new EventEmitter<NamedFragmentRecords>();


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

	changeProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new NamedFragmentRecords();
		if ( typeof property.modified !== "undefined" ) {
			this.records.changes.set( property.modified.id, property );
		} else {
			this.records.changes.delete( property.copy.id );
		}
		this.namedFragmentChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	deleteProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new NamedFragmentRecords();
		if ( typeof property.added !== "undefined" ) {
			this.records.additions.delete( property.added.id );
			this.properties.splice( index, 1 );
		} else if ( typeof property.deleted !== "undefined" ) {
			this.records.deletions.set( property.deleted.id, property );
		}
		this.namedFragmentChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	addProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new NamedFragmentRecords();
		if ( typeof property.added !== "undefined" ) {
			if ( property.added.id === property.added.name ) {
				this.records.additions.set( property.added.id, property );
			} else {
				this.records.additions.delete( property.added.id );
				this.records.additions.set( property.added.name, property );
			}
		}
		this.namedFragmentChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
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
		Object.keys( this.namedFragment ).forEach( ( propName:string )=> {
			this.properties.push( <PropertyRow>{
				copy: <Property>{
					id: propName,
					name: propName,
					value: this.namedFragment[ propName ]
				}
			} );
		} );
	}

}
// export interface WorkingNamedFragment {
// 	copy:any;
// 	added?:any;
// 	modified?:any;
// 	deleted?:any;
// }
export class NamedFragment {
	id:string;
	properties:Property[];
}
export class NamedFragmentRecords {
	changes:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	deletions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	additions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
}