import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

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

	@Input() type:string = NS.XSD.DataType.string;
	@Input() value:string|number|boolean = "";
	@Output() onIsValid:EventEmitter<boolean> = new EventEmitter<boolean>();


	constructor() {}

	ngOnInit():void {
		this.input = new Control( this.value, Validators.compose( [ Validators.required, this.validateInput.bind( this ) ] ) );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( ! ! changes[ "type" ] && changes[ "type" ].currentValue !== changes[ "type" ].previousValue ) {
			this.input.updateValueAndValidity();
		}
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
		if ( ! isNaN( value ) ) value = Number( value );
		switch ( this.type ) {
			case NS.XSD.DataType.boolean:
				valid = Utils.isBoolean( value );
				break;
			case NS.XSD.DataType.string:
				valid = Utils.isString( value );
				break;
			case NS.XSD.DataType.int:
				valid = Utils.isInteger( value );
				break;
			case NS.XSD.DataType.integer:
				valid = Utils.isInteger( value );
				break;
			case NS.XSD.DataType.double:
				valid = Utils.isDouble( value );
				break;
			case NS.XSD.DataType.decimal:
				valid = Utils.isNumber( value );
				break;
			case NS.XSD.DataType.date:
				valid = Utils.isDate( value );
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