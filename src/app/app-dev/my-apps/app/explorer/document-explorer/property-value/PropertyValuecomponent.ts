import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import { IterableMapPipe } from "./../iterable-map/IterableMapPipe"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "property-value",
	template: template,
	host: { "[class.error]": "!!input && (input.touched || input.dirty) && !input.valid" },
	encapsulation: ViewEncapsulation.Emulated,
	pipes: [ IterableMapPipe ]
} )

export default class PropertyValueComponent {

	$element:JQuery;
	element:ElementRef;
	$fragmentsDropdown:JQuery;
	modes:Modes = Modes;

	@Input() mode:string = Modes.READ;
	@Input() type:string = NS.XSD.DataType.string;
	@Input() defaultValue:string = "";
	@Input() placeholder:string;

	@Input() bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	@Input() namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();

	@Output() onValueChange:EventEmitter<string> = new EventEmitter<string>();
	@Output() onError:EventEmitter<string> = new EventEmitter<string>();
	@Output() onChangeProperty:EventEmitter<string> = new EventEmitter<string>();

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

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$fragmentsDropdown = this.$element.find( ".fragments.dropdown.search" );
		if ( ! ! this.$fragmentsDropdown ) {
			this.$fragmentsDropdown.dropdown( {
				allowAdditions: true,
				onChange: this.onChangeValue.bind( this )
			} );
		}
	}

	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		(<Control>this.input).updateValue( this.defaultValue );
		this.mode = Modes.READ;
	}

	save():void {
		this.mode = Modes.READ;
		this.onChangeProperty.emit( this.input.value );
	}

	onChangeValue( value:string, text:string, choice:JQuery ):void {
		console.log( "value: %o, text:%o, choice:%o", value, text, choice );
		(<Control>this.input).updateValue( value );
	}

	onFragmentHover( $event:Event, fragment ):void {
		console.log( "Event: %o, fragment:%o", $event, fragment );
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

	getFriendlyName( uri:string ):string {
		if ( URI.Util.hasFragment( uri ) )return URI.Util.getFragment( uri );
		return URI.Util.getSlug( uri );
	}

}

export class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}