import { Component, ViewChild, Input, Output, EventEmitter } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as Literal from "carbonldp/RDF/Literal";


import template from "./template.html!";

@Component( {
	selector: "tr.literal",
	template: template,
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
	// value:string|boolean|number = "";
	type:string = "";
	language:string = "";
	isStringType:boolean = (! this.type || this.type === NS.XSD.DataType.string);


	private _value:string|boolean|number = "";
	get value() {return this._value;}

	@Input() set value( value:Literal ) {
		this._value = value;
		if ( ! ! this.valueInput && this.valueInput.value !== this.value )(<Control>this.valueInput).updateValue( this.value );
	}

	private _literal = <Literal>{};
	get literal() { return this._literal; }

	@Input() set literal( value:Literal ) {
		this._literal = value;
		this.value = ! ! this.tempLiteral[ "@value" ] ? this.tempLiteral[ "@value" ] : this.literal[ "@value" ];
		this.type = ! ! this.tempLiteral[ "@type" ] ? this.tempLiteral[ "@type" ] : this.literal[ "@type" ];
		this.language = ! ! this.tempLiteral[ "@language" ] ? this.tempLiteral[ "@language" ] : this.literal[ "@language" ];
	}

	@Input() canDisplayLanguage:boolean = false;
	@Output() onEditMode:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onSave:EventEmitter<any> = new EventEmitter<any>();

	private tempLiteral:any = {};
	valueInput:AbstractControl = new Control( this.value, Validators.compose( [ Validators.required, this.valueValidator.bind( this ) ] ) );

	constructor() {}

	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		this.mode = Modes.READ;
		this.tempLiteral = <Literal>{};
	}

	save():void {

		this.mode = Modes.READ;
	}

	changeValue( value:string|number|boolean ):void {
		// this.value = this.literalValueComponent.getParsedValue( value );
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


	private getParsedValue( value:string|boolean|number ):string|boolean|number {
		if ( typeof value === "undefined" && ! ! this.input ) value = this.input.value.toLowerCase().trim();
		switch ( this.type ) {
			// case NS.XSD.DataType.boolean:
			// 	value = Utils.parseBoolean( value );
			// 	break;
			// case NS.XSD.DataType.int:
			// case NS.XSD.DataType.integer:
			// case NS.XSD.DataType.double:
			// case NS.XSD.DataType.decimal:
			// 	value = Number( value );
			// 	break;

			// Boolean
			case NS.XSD.DataType.boolean:
				value = Utils.isBoolean( Literal.Factory.parse( value, this.type ) ) ? Literal.Factory.parse( value, this.type ) : value;
				break;

			// Numbers
			case NS.XSD.DataType.int :
			case NS.XSD.DataType.integer :
				value = ! isNaN( value ) && ! isNaN( Literal.Factory.parse( value, this.type ) ) && Utils.isInteger( Literal.Factory.parse( value, this.type ) ) ? Literal.Factory.parse( value, this.type ) : value;
				break;

			case NS.XSD.DataType.byte :
			case NS.XSD.DataType.decimal :
			case NS.XSD.DataType.long :
			case NS.XSD.DataType.negativeInteger :
			case NS.XSD.DataType.nonNegativeInteger :
			case NS.XSD.DataType.nonPositiveInteger :
			case NS.XSD.DataType.positiveInteger :
			case NS.XSD.DataType.short :
			case NS.XSD.DataType.unsignedLong :
			case NS.XSD.DataType.unsignedInt :
			case NS.XSD.DataType.unsignedShort :
			case NS.XSD.DataType.unsignedByte :
			case NS.XSD.DataType.double :
			case NS.XSD.DataType.float :
				value = ! isNaN( value ) && ! isNaN( Literal.Factory.parse( value, this.type ) ) && Utils.isNumber( Literal.Factory.parse( value, this.type ) ) ? Literal.Factory.parse( value, this.type ) : value;
				break;

			// Dates
			case NS.XSD.DataType.date:
			case NS.XSD.DataType.dateTime:
			case NS.XSD.DataType.time:
				value = Utils.isDate( Literal.Factory.parse( value, this.type ) ) ? Literal.Factory.parse( value, this.type ) : value;
				break;

			default:
				break;
		}
		return value;
	}

	private valueValidator( control:AbstractControl ):any {
		let valid:boolean;
		switch ( this.type ) {
			// Boolean
			case NS.XSD.DataType.boolean:
				switch ( control.value ) {
					case "true":
					case "yes":
					case "y":
					case "1":
					case "false":
					case "no":
					case "n":
					case "0":
						valid = true;
				}
				// valid = Utils.isBoolean( Literal.Factory.parse( control.value, this.type ) );
				break;

			// Numbers
			case NS.XSD.DataType.int :
			case NS.XSD.DataType.integer :
				valid = ! isNaN( control.value ) && ! isNaN( Literal.Factory.parse( control.value, this.type ) ) && Utils.isInteger( Literal.Factory.parse( control.value, this.type ) );
				break;

			case NS.XSD.DataType.byte :
			case NS.XSD.DataType.decimal :
			case NS.XSD.DataType.long :
			case NS.XSD.DataType.negativeInteger :
			case NS.XSD.DataType.nonNegativeInteger :
			case NS.XSD.DataType.nonPositiveInteger :
			case NS.XSD.DataType.positiveInteger :
			case NS.XSD.DataType.short :
			case NS.XSD.DataType.unsignedLong :
			case NS.XSD.DataType.unsignedInt :
			case NS.XSD.DataType.unsignedShort :
			case NS.XSD.DataType.unsignedByte :
			case NS.XSD.DataType.double :
			case NS.XSD.DataType.float :
				valid = ! isNaN( control.value ) && ! isNaN( Literal.Factory.parse( control.value, this.type ) ) && Utils.isNumber( Literal.Factory.parse( control.value, this.type ) );
				break;

			// Dates
			case NS.XSD.DataType.date:
			case NS.XSD.DataType.dateTime:
			case NS.XSD.DataType.time:
				valid = Utils.isDate( Literal.Factory.parse( control.value, this.type ) );
				break;

			case NS.XSD.DataType.string:
				valid = Utils.isString( Literal.Factory.parse( control.value, this.type ) );
				break;

			default:
				valid = Utils.isString( control.value );
				break;
		}
		if ( ! valid ) {
			return { "invalidTypeError": true };
		}
		return null;
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
