import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as URI from "carbonldp/RDF/URI";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "literal-type",
	template: template,
	host: { "[class.error]": "!!input && !input.valid" },
	encapsulation: ViewEncapsulation.Emulated
} )

export default class LiteralTypeComponent {

	element:ElementRef;
	$element:JQuery;
	$dataTypesSearch:JQuery;
	dataTypes:any = this.getDataTypes();
	input:AbstractControl;

	modes:Modes = Modes;
	private _mode = NS.XSD.DataType.string;
	@Input() set mode( value:string ) {
		this._mode = value;
		if ( this.mode === Modes.EDIT )this.initializeDropdown();
	}

	get mode() {
		return this._mode;
	}

	private _type = NS.XSD.DataType.string;
	@Input() set type( value:string ) {
		if ( ! value || value.length === 0 ) value = NS.XSD.DataType.string;
		this._type = value;
	}

	get type() {
		return this._type;
	}

	@Input() shouldSave:EventEmitter<boolean> = new EventEmitter<boolean>();


	@Output() onIsValid:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onTypeSelected:EventEmitter<PropertyType> = new EventEmitter<PropertyType>();


	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.input = new Control( this.type, Validators.compose( [ Validators.required, this.validateSelectionInput.bind( this ) ] ) );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	initializeDropdown():void {
		this.$dataTypesSearch = $( this.element.nativeElement.querySelector( ".search.dropdown.data-types" ) );
		this.$dataTypesSearch.dropdown( {
			allowAdditions: true,
			onChange: this.onChangeType.bind( this )
		} );
		this.$dataTypesSearch.dropdown( "set selected", this.type );
	}

	onChangeType( value:string, text:string, choice:JQuery ):void {
		(<Control>this.input).updateValue( value === "empty" ? "" : value );
		this.onTypeSelected.emit( { name: text, value: value } );
	}

	getDataTypes():any {
		let dataTypes:any[] = [];
		let xsdDataTypes:any[] = this.getXSDDataTypes();
		dataTypes = dataTypes.concat( xsdDataTypes );
		return dataTypes;
	}

	getXSDDataTypes():any[] {
		let xsdDataTypes:any[] = [];
		Utils.forEachOwnProperty( NS.XSD.DataType, ( key:string, value:any ):void => {
			if ( URI.Util.isAbsolute( key ) ) {
				xsdDataTypes.push( {
					title: value,
					description: NS.XSD.DataType[ value ],
					value: NS.XSD.DataType[ value ],
				} );
			}
		} );
		return xsdDataTypes;
	}


	validateSelectionInput( control:AbstractControl ):any {
		if ( ! control.value ) {
			this.onIsValid.emit( false );
			return { "emptySelectionError": true };
		}
		this.onIsValid.emit( true );
		return null;
	}
}
interface PropertyType {
	name:string,
	value:string
}
class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}