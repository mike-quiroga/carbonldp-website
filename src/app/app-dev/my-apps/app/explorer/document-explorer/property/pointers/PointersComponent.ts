import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { IterableMapPipe } from "./../../iterable-map/IterableMapPipe";
import PointerComponent from "./pointer/PointerComponent";
import { Pointer, PointerRow, Modes } from "./pointer/PointerComponent";

import template from "./template.html!";
// import "./style.css!";

@Component( {
	selector: "pointers",
	template: template,
	encapsulation: ViewEncapsulation.Emulated,
	directives: [ PointerComponent ],
	pipes: [ IterableMapPipe ],
} )

export default class PointersComponent {

	modes:Modes = Modes;
	tokens:string[] = [ "@id", "@type" ];
	tempPointers:Pointer[] = [];
	isEditingPointer:boolean = false;
	@Input() pointers:PointerRow[] = [];
	@Input() bNodes:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	@Input() namedFragments:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	@Output() onPointersChanges:EventEmitter<PointerRow[]> = new EventEmitter<PointerRow[]>();

	constructor() { }

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

	deletePointer( deletingPointer:PointerRow, index:number ):void {
		this.onPointersChanges.emit( this.pointers );
	}

	deleteNewPointer( deletingPointer:PointerRow, index:number ):void {
		this.pointers.splice( index, 1 );
		this.onPointersChanges.emit( this.pointers );
	}

	canDisplayPointers():boolean {
		return this.getUntouchedPointers().length > 0;
	}

	getPointers():PointerRow[] {
		return this.pointers.filter( ( pointer:PointerRow ) => typeof pointer.copy !== "undefined" );
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
}