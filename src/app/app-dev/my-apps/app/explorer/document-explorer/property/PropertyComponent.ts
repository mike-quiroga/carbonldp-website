import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

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
	host: { "[class.has-changed]": "property.modified", "[class.deleted-property]": "property.deleted", "[class.added-property]": "property.added" },
} )

export default class PropertyComponent {

	element:ElementRef;
	$element:JQuery;
	literals:LiteralRow[];
	pointers:PointerRow[];
	tempLiterals:LiteralRow[];
	tempPointers:PointerRow[];
	tempProperty:Property = <Property>{};
	copyOrAdded:string;
	id:string;
	name:string;
	value:any[] = [];
	addNewLiteral:EventEmitter<boolean> = new EventEmitter<boolean>();
	addNewPointer:EventEmitter<boolean> = new EventEmitter<boolean>();
	commonHeaders:string[] = [ "@id", "@type", "@value" ];
	modes:Modes = Modes;
	nameInput:AbstractControl = new Control( this.name, Validators.compose( [ Validators.required, this.nameValidator.bind( this ) ] ) );
	@Input() mode:string = Modes.READ;
	@Input() documentURI:string;
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() canEdit:boolean = true;

	@Output() onGoToBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onGoToNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<Property> = new EventEmitter<Property>();
	@Output() onDeleteProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	@Output() onDeleteNewProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	@Output() onSaveNewProperty:EventEmitter<PropertyRow> = new EventEmitter<PropertyRow>();
	private _property:PropertyRow;
	@Input() set property( prop:PropertyRow ) {
		this.copyOrAdded = typeof prop.copy !== "undefined" ? "copy" : "added";
		this._property = prop;
		this.id = prop[ this.copyOrAdded ].id;
		this.tempProperty.id = prop[ this.copyOrAdded ].id;
		this.name = prop[ this.copyOrAdded ].name;
		(<Control>this.nameInput).updateValue( this.name );
		if ( Utils.isArray( prop[ this.copyOrAdded ].value ) ) {
			this.value = [];
			prop[ this.copyOrAdded ].value.forEach( ( literalOrRDFNode )=> { this.value.push( Object.assign( literalOrRDFNode ) ) } )
		} else {
			this.value = prop[ this.copyOrAdded ].value;
		}
	}

	get property():PropertyRow { return this._property; }

	nameHasChanged:boolean = false;
	literalsHaveChanged:boolean = false;
	pointersHaveChanged:boolean = false;

	get propertyHasChanged():boolean { return this.nameHasChanged || this.literalsHaveChanged || this.pointersHaveChanged; }

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		if ( Utils.isArray( this.value ) ) this.fillLiteralsAndPointers();
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.initializeAccordions();
		this.initializePropertyButtons();
		this.initializeDeletionDimmer();
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

	initializePropertyButtons():void {
		this.$element.find( ".ui.options.dropdown.button" ).dropdown( {
			transition: "swing up"
		} );
	}

	initializeDeletionDimmer():void {
		this.$element.find( ".confirm-deletion.dimmer" ).dimmer( { closable: false } );
	}

	onEditName():void {
		this.mode = Modes.EDIT;
	}

	cancelDeletion():void {
		this.$element.find( ".confirm-deletion.dimmer" ).dimmer( "hide" );
	}

	askToConfirmDeletion():void {
		this.$element.find( ".confirm-deletion.dimmer" ).dimmer( "show" );
	}

	deleteProperty():void {
		if ( typeof this.property.added !== "undefined" ) {
			this.onDeleteNewProperty.emit( this.property );
		} else {
			this.property.deleted = this.property.copy;
			this.onDeleteProperty.emit( this.property );
		}
	}

	cancel():void {
		if ( this.nameInput.valid ) {
			this.mode = Modes.READ;
			(<Control>this.nameInput).updateValue( this.name );
		}
	}

	save():void {
		this.name = this.nameInput.value;

		if ( typeof this.name !== "undefined" && (this.name !== this.property[ this.copyOrAdded ].name || this.name !== this.tempProperty.name ) ) {
			this.tempProperty.name = this.name;
			this.changePropertyValues( this.tempPointers.concat( this.tempLiterals ) );
		}
		this.mode = Modes.READ;
	}

	fillLiteralsAndPointers():void {
		this.literals = [];
		this.tempLiterals = [];
		this.pointers = [];
		this.tempPointers = [];
		this.property[ this.copyOrAdded ].value.forEach( ( literalOrRDFNode )=> {
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

	addLiteral():void {
		// Notify LiteralsComponent to add literal
		this.addNewLiteral.emit( true );
	}

	addPointer():void {
		// Notify PointersComponent to add pointer
		this.addNewPointer.emit( true );
	}

	checkForChangesOnLiterals( literals:LiteralRow[] ):void {
		this.tempLiterals = literals;
		this.changePropertyValues( this.tempPointers.concat( this.tempLiterals ) );
		this.literalsHaveChanged = ! ! literals.find( ( literalRow )=> {return ! ! literalRow.modified || ! ! literalRow.added || ! ! literalRow.deleted } );
		if ( ! this.propertyHasChanged ) delete this.property.modified;
	}

	checkForChangesOnPointers( pointers:PointerRow[] ):void {
		this.tempPointers = pointers;
		this.changePropertyValues( this.tempPointers.concat( this.tempLiterals ) );
		this.pointersHaveChanged = ! ! pointers.find( ( pointerRow )=> {return ! ! pointerRow.modified || ! ! pointerRow.added || ! ! pointerRow.deleted } );
		if ( ! this.propertyHasChanged ) delete this.property.modified;
	}

	changePropertyValues( literalsOrPointers:LiteralRow[]|PointerRow[] ):void {
		this.tempProperty.id = this.id;
		this.tempProperty.name = this.name;
		// Change name
		if ( (! ! this.property.copy) ) {
			if ( (this.tempProperty.name !== this.property.copy.name ) ) {
				this.property.modified = this.tempProperty;
				this.nameHasChanged = true;
			} else { this.nameHasChanged = false; }
		}

		// Change literals and pointers
		if ( Utils.isArray( this.value ) ) {
			this.tempProperty.value = [];
			literalsOrPointers.forEach( ( literalOrPointerRow )=> {
				if ( ! literalOrPointerRow.deleted )this.tempProperty.value.push( ! ! literalOrPointerRow.added ? literalOrPointerRow.added : ! ! literalOrPointerRow.modified ? literalOrPointerRow.modified : literalOrPointerRow.copy );
			} );
		} else {
			this.tempProperty.value = this.value;
		}
		if ( ! ! this.property.copy ) {
			this.property.modified = this.tempProperty;
			this.onChangeProperty.emit( this.tempProperty );
		} else if ( ! ! this.property.added ) {
			if ( (this.tempProperty.name !== this.property.added.name ) ) {
				this.id = this.name;
				this.tempProperty.id = this.id;
			}
			this.property.added = this.tempProperty;
			this.onSaveNewProperty.emit( this.tempProperty );
		}
	}

	private nameValidator( control:AbstractControl ):any {
		if ( ! ! control ) {
			if ( typeof control.value === "undefined" || control.value === null || ! control.value ) return null;
			if ( ! /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test( control.value ) )
				return { "invalidName": true };
			if ( control.value.split( "#" ).length > 2 ) return { "duplicatedHashtag": true };
			if ( ! URI.Util.hasFragment( control.value ) ) return { "missingFragment": true };
		}
		return null;
	}
}

export interface PropertyRow {
	copy:any;
	added?:any;
	modified?:any;
	deleted?:any;
}

export interface Property {
	id:string;
	name:string;
	value:any[];
}

export class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}
