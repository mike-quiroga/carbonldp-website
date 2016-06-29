import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { IterableMapPipe } from "./../../iterable-map/IterableMapPipe";
import LiteralComponent from "./literal/LiteralComponent";
import { Literal, LiteralRow, Modes } from "./literal/LiteralComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "literals",
	template: template,
	encapsulation: ViewEncapsulation.Emulated,
	directives: [ LiteralComponent ],
	pipes: [ IterableMapPipe ],
} )

export default class LiteralsComponent {

	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	tempLiterals:Literal[] = [];
	modifiedLiterals:{id:number, literal:Literal}[] = [];
	addedLiterals:LiteralRow[] = [];
	deletedLiterals:Literal[] = [];
	isLanguagePresent:boolean = false;
	isEditingLiteral:boolean = false;
	@Input() literals:LiteralRow[] = [];
	@Output() onLiteralsChanges:EventEmitter<Literal[]> = new EventEmitter<Literal[]>();

	constructor() {}

	ngOnInit():void {
		this.isLanguagePresent = this.existsToken( "@language" );
	}

	existsToken( token:string ):boolean {
		return ! ! this.literals.find( ( literal:any )=> {
			return (! ! literal.added && typeof literal.added[ token ] !== "undefined")
				|| (! ! literal.modified && typeof literal.modified[ token ] !== "undefined")
				|| (! ! literal.copy && typeof literal.copy[ token ] !== "undefined")
		} );
	}

	editModeChanged( value:boolean ):void {
		this.isEditingLiteral = value;
	}

	saveLiteral( modifiedLiteral:Literal, originalLiteral:Literal, index:number ) {
		if ( modifiedLiteral.hasOwnProperty( "@value" ) ) {
			this.literals[ index ].modified = modifiedLiteral;
		}
		this.isLanguagePresent = this.existsToken( "@language" );
		this.onLiteralsChanges.emit( this.literals );
	}

	saveNewLiteral( newLiteral:Literal, originalLiteral:Literal, index:number ) {
		if ( newLiteral.hasOwnProperty( "@value" ) ) {
			this.literals[ index ].added = newLiteral;
		}
		this.isLanguagePresent = this.existsToken( "@language" );
		this.onLiteralsChanges.emit( this.literals );
	}

	addNewLiteral():void {
		let newLiteralRow:LiteralRow = <LiteralRow>{};
		newLiteralRow.added = <Literal>{};
		this.literals.push( newLiteralRow );
	}

	deleteNewLiteral( deletingLiteral:LiteralRow, index:number ):void {
		this.literals.splice( index, 1 );
		this.onLiteralsChanges.emit( this.literals );
	}

	deleteLiteral( deletingLiteral:LiteralRow, index:number ):void {
		this.onLiteralsChanges.emit( this.literals );
	}

	canDisplayLiterals():boolean {
		return this.getUntouchedLiterals().length > 0 || this.getAddedLiterals().length > 0 || this.getModifiedLiterals().length > 0;
	}

	getLiterals():LiteralRow[] {
		return this.literals.filter( ( literal:LiteralRow ) => typeof literal.copy !== "undefined" );
	}

	getAddedLiterals():LiteralRow[] {
		return this.literals.filter( ( literal:LiteralRow ) => typeof literal.added !== "undefined" );
	}

	getModifiedLiterals():LiteralRow[] {
		return this.literals.filter( ( literal:LiteralRow ) => typeof literal.modified !== "undefined" );
	}

	getDeletedLiterals():LiteralRow[] {
		return this.literals.filter( ( literal:LiteralRow ) => typeof literal.deleted !== "undefined" );
	}

	getUntouchedLiterals():LiteralRow[] {
		return this.literals.filter( ( literal:LiteralRow ) => typeof literal.modified === "undefined" && typeof literal.deleted === "undefined" );
	}

	// getLiterals():void {
	// 	this.literals.forEach( ( literal:Literal, index:number )=> {
	// 		this.tempLiterals.set( index, Object.assign( {}, literal ) );
	// 	} );
	// 	this.modifiedLiterals.forEach( ( modifiedLiteral:{key:number, value:Literal}, index:number )=> {
	// 		this.tempLiterals.set( index, literal );
	// 	} );
	// }

	canDisplayLanguage():boolean {
		return this.isLanguagePresent || this.isEditingLiteral;
	}
}