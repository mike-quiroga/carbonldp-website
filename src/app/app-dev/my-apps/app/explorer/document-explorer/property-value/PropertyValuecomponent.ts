import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";

import template from "./template.html!";

@Component( {
	selector: "property-value",
	template: template,
	host: { "[class.error]": "!!input && (input.touched || input.dirty) && !input.valid" },
	encapsulation: ViewEncapsulation.Emulated
} )

export default class PropertyInputComponent {

	element:ElementRef;


	@Input() type:string = NS.XSD.DataType.string;
	@Input() defaultValue:string = "";
	@Input() placeholder:string;
	@Output() onValueChange:EventEmitter<string> = new EventEmitter<string>();
	@Output() onError:EventEmitter<string> = new EventEmitter<string>();

	input:AbstractControl = new Control( this.defaultValue, Validators.compose( [ Validators.required, this.validateInput.bind( this ) ] ) );


	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.input = new Control( this.defaultValue, Validators.compose( [ Validators.required, this.validateInput.bind( this ) ] ) );
		this.input.valueChanges
			.debounceTime( 400 )
			.distinctUntilChanged()
			.subscribe( ( value:string )=> {
				this.onValueChange.emit( value );
			} );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "type" ].currentValue !== changes[ "type" ].previousValue ) {
			this.input.updateValueAndValidity();
		}
	}

	private validateInput( valueOrControl:string|AbstractControl ):any {
		let valid:boolean, value:any;
		value = valueOrControl;
		if ( typeof valueOrControl !== "string" ) {
			value = (<AbstractControl>valueOrControl).value;
			if ( valueOrControl.touched && ! ! value && value.trim().length === 0 ) {
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
		if ( ! valid ) return { "invalidTypeError": true };
		return null;
	}

}