import { Component, ViewChild, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";
import * as NS from "carbonldp/NS";


import LiteralValueComponent from "./literal-value/LiteralValueComponent";
import LiteralTypeComponent from "./literal-type/LiteralTypeComponent";
import LiteralLanguageComponent from "./literal-language/LiteralLanguageComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "tr.literal",
	template: template,
	directives: [ LiteralValueComponent, LiteralTypeComponent, LiteralLanguageComponent ],
} )

export default class LiteralComponent {

	private _mode = Modes.READ;
	set mode( value:string ) {
		this._mode = value;
		this.onEditMode.emit( this.mode === Modes.EDIT );
	}

	get mode() {
		return this._mode;
	}

	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	isValidValue:boolean = false;
	isValidType:boolean = false;
	isValidLanguage:boolean = false;
	isStringType:boolean = false;

	@Input() literal:Literal;
	@Input() canDisplayLanguage:boolean = false;
	@Output() onEditMode:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onSave:EventEmitter<any> = new EventEmitter<any>();
	@ViewChild( LiteralValueComponent ) literalValue:LiteralValueComponent;
	@ViewChild( LiteralTypeComponent ) literalType:LiteralTypeComponent;
	@ViewChild( LiteralLanguageComponent ) literalLanguage:LiteralLanguageComponent;

	private tempLiteral:any = {};

	constructor() {}

	ngOnInit():void {
		this.isStringType = (! this.literal[ "@type" ] || this.literal[ "@type" ] === NS.XSD.DataType.string);
	}

	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		this.mode = Modes.READ;
	}

	save():void {
		if ( ! ! this.literalValue ) this.changeValue( this.literalValue.input.value );
		if ( ! ! this.literalType ) this.changeType( this.literalType.input.value );
		if ( ! ! this.literalLanguage ) this.changeLanguage( this.literalLanguage.input.value );
		this.onSave.emit( this.tempLiteral );
	}

	changeValue( value:string|number|boolean ):void {
		this.tempLiteral[ "@value" ] = value;
	}

	changeType( type:string ):void {
		type = type === NS.XSD.DataType.string ? null : type;
		this.isStringType = ! type;
		if ( ! ! type ) {
			this.tempLiteral[ "@type" ] = type;
			delete this.tempLiteral[ "@language" ];
		}
	}

	changeLanguage( value:string ):void {
		delete this.tempLiteral[ "@language" ];
		if ( ! ! value ) this.tempLiteral[ "@language" ] = value;
	}

	onIsValidValue( isValid:boolean ):void {
		this.isValidValue = isValid;
	}

	onIsValidType( isValid:boolean ):void {
		this.isValidType = isValid;
	}

	onIsValidLanguage( isValid:boolean ):void {
		this.isValidLanguage = isValid;
	}

}
class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}
export interface Literal {
	value:string|number|boolean;
	type:string;
	language:string;
}
