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
import { Literal, LiteralRow } from "./literal/LiteralComponent";

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

	tokens:string[] = [ "@value", "@type", "@language" ];
	tempLiterals:Literal[] = [];
	modifiedLiterals:{id:number, literal:Literal}[] = [];
	addedLiterals:Literal[] = [];
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
		return ! ! this.literals.find( ( literal:any )=> {return ! ! literal.copy[ token ] || (! ! literal.modified && ! ! literal.modified[ token ]) } );
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