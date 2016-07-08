import { Component, Input, Output, EventEmitter } from "@angular/core";

import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PointerComponent from "./pointer.component";
import { Modes } from "./../property/property.component";
import { Pointer, PointerRow } from "./pointer.component";

import template from "./pointers.component.html!";
import style from "./pointers.component.css!text";

@Component( {
	selector: "pointers",
	template: template,
	styles: [ style ],
	directives: [ PointerComponent ],
} )

export default class PointersComponent {

	modes:Modes = Modes;
	tokens:string[] = [ "@id", "@type" ];
	tempPointers:Pointer[] = [];
	isEditingPointer:boolean = false;
	@Input() documentURI:string = "";
	@Input() pointers:PointerRow[] = [];
	@Input() onAddNewPointer:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;

	@Output() onPointersChanges:EventEmitter<PointerRow[]> = new EventEmitter<PointerRow[]>();
	@Output() onGoToBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onGoToNamedFragment:EventEmitter<string> = new EventEmitter<string>();

	constructor() { }

	ngOnInit():void {
		this.onAddNewPointer.subscribe( ()=> {
			this.addNewPointer();
		} );
	}

	addNewPointer():void {
		let newPointerRow:PointerRow = <PointerRow>{};
		newPointerRow.added = <Pointer>{};
		this.pointers.push( newPointerRow );
	}

	savePointer( modifiedPointer:Pointer, originalPointer:Pointer, index:number ) {
		if ( modifiedPointer.hasOwnProperty( "@id" ) ) {
			this.pointers[ index ].modified = modifiedPointer;
		}
		this.onPointersChanges.emit( this.pointers );
	}

	saveNewPointer( newPointer:Pointer, originalPointer:Pointer, index:number ) {
		if ( newPointer.hasOwnProperty( "@id" ) ) {
			this.pointers[ index ].added = newPointer;
		}
		this.onPointersChanges.emit( this.pointers );
	}

	deletePointer( deletingPointer:PointerRow, index:number ):void {
		this.onPointersChanges.emit( this.pointers );
	}

	deleteNewPointer( deletingPointer:PointerRow, index:number ):void {
		this.pointers.splice( index, 1 );
		this.onPointersChanges.emit( this.pointers );
	}

	canDisplayPointers():boolean {
		return this.getUntouchedPointers().length > 0 || this.getAddedPointers().length > 0 || this.getModifiedPointers().length > 0;
	}

	getAddedPointers():PointerRow[] {
		return this.pointers.filter( ( pointer:PointerRow ) => typeof pointer.added !== "undefined" );
	}

	getModifiedPointers():PointerRow[] {
		return this.pointers.filter( ( pointer:PointerRow ) => typeof pointer.modified !== "undefined" );
	}

	getDeletedPointers():PointerRow[] {
		return this.pointers.filter( ( pointer:PointerRow ) => typeof pointer.deleted !== "undefined" );
	}

	getUntouchedPointers():PointerRow[] {
		return this.pointers.filter( ( pointer:PointerRow ) => typeof pointer.modified === "undefined" && typeof pointer.deleted === "undefined" );
	}

	goToBNode( id:string ):void {
		this.onGoToBNode.emit( id );
	}

	goToNamedFragment( id:string ):void {
		this.onGoToNamedFragment.emit( id );
	}
}