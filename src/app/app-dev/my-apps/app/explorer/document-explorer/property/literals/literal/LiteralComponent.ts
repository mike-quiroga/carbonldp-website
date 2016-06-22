import { Component, ViewChild, Input, Output, EventEmitter } from "@angular/core";

import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";


import LiteralValueComponent from "./literal-value/LiteralValueComponent";
import LiteralTypeComponent from "./literal-type/LiteralTypeComponent";
import LiteralLanguageComponent from "./literal-language/LiteralLanguageComponent";

import template from "./template.html!";

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
	isValidValue:boolean = false;
	isValidType:boolean = false;
	isValidLanguage:boolean = false;
	isStringType:boolean = false;
	value:string|boolean|number = "";
	type:string = "";
	language:string = "";

	private _literal = <Literal>{};
	@Input() set literal( value:Literal ) {
		this._literal = value;
		this.value = this.literal[ "@value" ];
		this.type = this.literal[ "@type" ];
		this.language = this.literal[ "@language" ];
	}

	get literal() {
		return this._literal;
	}

	@Input() canDisplayLanguage:boolean = false;
	@Output() onEditMode:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onSave:EventEmitter<any> = new EventEmitter<any>();
	@ViewChild( LiteralValueComponent ) literalValueComponent:LiteralValueComponent;
	@ViewChild( LiteralTypeComponent ) literalTypeComponent:LiteralTypeComponent;
	@ViewChild( LiteralLanguageComponent ) literalLanguageComponent:LiteralLanguageComponent;

	private tempLiteral:any = {};

	constructor() {}

	ngOnInit():void {
		this.isStringType = (! this.literal[ "@type" ] || this.literal[ "@type" ] === NS.XSD.DataType.string);
	}

	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
		this.value = this.literal[ "@value" ];
		this.type = this.literal[ "@type" ];
		this.language = this.literal[ "@language" ];
		this.isStringType = (! this.literal[ "@type" ] || this.literal[ "@type" ] === NS.XSD.DataType.string);
	}

	cancelEdit():void {
		this.mode = Modes.READ;
		this.value = this.literal[ "@value" ];
		this.type = this.literal[ "@type" ];
		this.language = this.literal[ "@language" ];
		this.isStringType = (! this.literal[ "@type" ] || this.literal[ "@type" ] === NS.XSD.DataType.string);
	}

	save():void {
		if ( ! ! this.literalValueComponent ) this.changeValue( this.literalValueComponent.input.value );
		if ( ! ! this.literalTypeComponent ) this.changeType( this.literalTypeComponent.input.value );
		if ( ! ! this.literalLanguageComponent ) this.changeLanguage( this.literalLanguageComponent.input.value );
		delete this.tempLiteral[ "@value" ];
		delete this.tempLiteral[ "@type" ];
		delete this.tempLiteral[ "@language" ];
		if ( ! ! this.value ) this.tempLiteral[ "@value" ] = this.value;
		if ( ! ! this.type && this.type !== NS.XSD.DataType.string ) this.tempLiteral[ "@type" ] = this.type;
		if ( ! ! this.language && (! this.type || this.type === NS.XSD.DataType.string ) ) this.tempLiteral[ "@language" ] = this.language;
		this.onSave.emit( this.tempLiteral );
	}

	changeValue( value:string|number|boolean ):void {
		this.value = this.literalValueComponent.getParsedValue();
	}

	changeType( type:string ):void {
		if ( type === "empty" || type === NS.XSD.DataType.string ) type = null;
		this.isStringType = (type === NS.XSD.DataType.string || ! type);
		if ( ! this.isStringType ) this.language = null;
		this.type = type;
	}

	changeLanguage( value:string ):void {
		if ( value === "empty" ) value = null;
		this.language = value;
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
	"@value":string|number|boolean;
	"@type":string;
	"@language":string;
}
