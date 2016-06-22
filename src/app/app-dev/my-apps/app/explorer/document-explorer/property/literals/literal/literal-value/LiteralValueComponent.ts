import { Component, ViewEncapsulation, Input, Output, EventEmitter } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as Literal from "carbonldp/RDF/Literal";

import { IterableMapPipe } from "./../../../../iterable-map/IterableMapPipe"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "literal-value",
	template: template,
	host: { "[class.error]": "!!input && (input.touched || input.dirty) && !input.valid" },
	encapsulation: ViewEncapsulation.Emulated,
	pipes: [ IterableMapPipe ]
} )

export default class LiteralValueComponent {

	modes:Modes = Modes;
	input:AbstractControl = new Control( this.value, Validators.compose( [ Validators.required, this.validateInput.bind( this ) ] ) );

	private _mode = NS.XSD.DataType.string;
	@Input() set mode( value:string ) {
		this._mode = value;
		if ( this.mode === Modes.READ )(<Control>this.input).updateValue( this.value );
	}

	get mode() {
		return this._mode;
	}

	private _type:string = NS.XSD.DataType.string;
	@Input() set type( value:string ) {
		if ( ! value ) value = NS.XSD.DataType.string;
		this._type = value;
		this.input.updateValueAndValidity();
	}

	get type() {
		return this._type;
	}

	@Input() value:string|number|boolean = "";
	@Output() onIsValid:EventEmitter<boolean> = new EventEmitter<boolean>();


	constructor() {}

	ngOnInit():void {
		this.input = new Control( this.value, Validators.compose( [ Validators.required, this.validateInput.bind( this ) ] ) );
	}


	getParsedValue():string|boolean|number {
		let value:string|boolean|number = this.input.value.toLowerCase().trim();
		switch ( this.type ) {
			case NS.XSD.DataType.boolean:
				value = Utils.parseBoolean( value );
				break;
			case NS.XSD.DataType.int:
			case NS.XSD.DataType.integer:
			case NS.XSD.DataType.double:
			case NS.XSD.DataType.decimal:
				value = Number( value );
				break;
			default:
				break;
		}
		return value;
	}

	private validateInput( valueOrControl:string|AbstractControl ):any {
		let valid:boolean, value:any;
		value = valueOrControl;
		if ( typeof valueOrControl !== "string" ) {
			value = (<AbstractControl>valueOrControl).value;
			if ( valueOrControl.touched && ! value ) {
				if ( ! ! this.onIsValid ) this.onIsValid.emit( false );
				return { "emptyError": true };
			}
		}
		switch ( this.type ) {
			// Boolean
			case NS.XSD.DataType.boolean:
				valid = Utils.isBoolean( Literal.Factory.parse( value, this.type ) );
				break;

			// Numbers
			case NS.XSD.DataType.int :
			case NS.XSD.DataType.integer :
				valid = ! isNaN( value ) && ! isNaN( Literal.Factory.parse( value, this.type ) ) && Utils.isInteger( Literal.Factory.parse( value, this.type ) );
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
				valid = ! isNaN( value ) && ! isNaN( Literal.Factory.parse( value, this.type ) ) && Utils.isNumber( Literal.Factory.parse( value, this.type ) );
				break;

			// Dates
			case NS.XSD.DataType.date:
			case NS.XSD.DataType.dateTime:
			case NS.XSD.DataType.time:
				valid = Utils.isDate( Literal.Factory.parse( value, this.type ) );
				break;

			case NS.XSD.DataType.string:
				valid = Utils.isString( Literal.Factory.parse( value, this.type ) );
				break;

			default:
				valid = Utils.isString( value );
				break;
		}
		if ( ! valid ) {
			if ( ! ! this.onIsValid ) this.onIsValid.emit( false );
			return { "invalidTypeError": true };
		}
		if ( ! ! this.onIsValid ) this.onIsValid.emit( true );
		return null;
	}

}

export class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}