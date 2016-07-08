import { Component, Input, Output, EventEmitter } from "@angular/core";

import "semantic-ui/semantic";

import LiteralComponent from "./literal.component";
import { Modes } from "./../property/property.component";
import { Literal, LiteralRow } from "./literal.component";

import template from "./literals.component.html!";
import style from "./literals.component.css!text";

@Component( {
	selector: "literals",
	template: template,
	styles: [ style ],
	directives: [ LiteralComponent ],
} )

export default class LiteralsComponent {

	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	tempLiterals:Literal[] = [];
	isLanguagePresent:boolean = false;
	isEditingLiteral:boolean = false;
	@Input() literals:LiteralRow[] = [];
	@Input() onAddNewLiteral:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Input() canEdit:boolean = true;
	@Output() onLiteralsChanges:EventEmitter<LiteralRow[]> = new EventEmitter<LiteralRow[]>();

	constructor() {}

	ngOnInit():void {
		this.isLanguagePresent = this.existsToken( "@language" );
		this.onAddNewLiteral.subscribe( ()=> {
			this.addNewLiteral();
		} );
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
		this.isLanguagePresent = this.existsToken( "@language" );
		this.onLiteralsChanges.emit( this.literals );
	}

	saveNewLiteral( newLiteral:Literal, originalLiteral:Literal, index:number ) {
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

	canDisplayLanguage():boolean {
		return this.isLanguagePresent || this.isEditingLiteral;
	}
}