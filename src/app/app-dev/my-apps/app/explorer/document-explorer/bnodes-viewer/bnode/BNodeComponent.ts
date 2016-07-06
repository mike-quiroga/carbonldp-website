import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { Property, PropertyRow, Modes } from "./../../property/PropertyComponent";
import PropertyComponent from "./../../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "bnode",
	template: template,
	directives: [ PropertyComponent ],
} )

export default class BNodeComponent {

	element:ElementRef;
	$element:JQuery;
	modes:Modes = Modes;
	properties:PropertyRow[] = [];
	records:BNodeRecords;
	private _bNodeHasChanged:boolean;
	set bNodeHasChanged( hasChanged:boolean ) {
		this._bNodeHasChanged = hasChanged;
		this.onChanges.emit( this.records );
	}

	get bNodeHasChanged() {
		return this._bNodeHasChanged;
	}

	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;
	@Input() documentURI:string = "";
	private _bNode:RDFNode.Class;
	@Input() set bNode( value:RDFNode.Class ) {
		this._bNode = value;
		this.getProperties();
	}

	get bNode() {
		return this._bNode;
	}

	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChanges:EventEmitter<BNodeRecords> = new EventEmitter<BNodeRecords>();


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
		if ( typeof this.records === "undefined" ) this.records = new BNodeRecords();
		if ( typeof property.modified !== "undefined" ) {
			this.records.changes.set( property.modified.id, property );
		} else {
			this.records.changes.delete( property.copy.id );
		}
		this.bNodeHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	deleteProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new BNodeRecords();
		if ( typeof property.added !== "undefined" ) {
			this.records.additions.delete( property.added.id );
			this.properties.splice( index, 1 );
		} else if ( typeof property.deleted !== "undefined" ) {
			this.records.deletions.set( property.deleted.id, property );
		}
		this.bNodeHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	addProperty( property:PropertyRow, index:number ):void {
		if ( typeof this.records === "undefined" ) this.records = new BNodeRecords();
		if ( typeof property.added !== "undefined" ) {
			if ( property.added.id === property.added.name ) {
				this.records.additions.set( property.added.id, property );
			} else {
				this.records.additions.delete( property.added.id );
				this.records.additions.set( property.added.name, property );
			}
		}
		this.bNodeHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
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
		if ( ! ! this.$element ) setTimeout( ()=>this.$element.find( "document-property.added-property" ).first().transition( "drop" ) );
	}

	getProperties():void {
		this.properties = [];
		Object.keys( this.bNode ).forEach( ( propName:string )=> {
			this.properties.push( <PropertyRow>{
				copy: <Property>{
					id: propName,
					name: propName,
					value: this.bNode[ propName ]
				}
			} );
		} );
	}

}
// export interface WorkingBNode {
// 	copy:any;
// 	added?:any;
// 	modified?:any;
// 	deleted?:any;
// }
export class BNode {
	id:string;
	properties:Property[];
}
export class BNodeRecords {
	changes:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	deletions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
	additions:Map<string,PropertyRow> = new Map<string, PropertyRow>();
}