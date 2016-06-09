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
	selector: "property-types",
	template: template,
	host: { "[class.error]": "!!input && !input.valid" },
	encapsulation: ViewEncapsulation.Emulated
} )

export default class PropertyTypesComponent {

	element:ElementRef;
	$element:JQuery;
	$dataTypesSearch:JQuery;
	dataTypes:any = this.getDataTypes();
	input:AbstractControl;

	@Input() initialValue:string = NS.XSD.DataType.string;
	@Output() onTypeSelected:EventEmitter<PropertyType> = new EventEmitter<PropertyType>();


	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.input = new Control( this.initialValue, Validators.compose( [ Validators.required, this.validateSelectionInput.bind( this ) ] ) );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$dataTypesSearch = this.$element.find( ".search.dropdown.data-types" );
		this.$dataTypesSearch.dropdown( {
			allowAdditions: true,
			onChange: this.onChangeType.bind( this )
		} );
		this.$dataTypesSearch.dropdown( "set selected", this.initialValue );
	}

	onChangeType( value:string, text:string, choice:JQuery ):void {
		(<Control>this.input).updateValue( value === "empty" ? "" : value );
		this.onTypeSelected.emit( { name: text, value: value } );
	}

	getDataTypes():any {
		let xsdDataTypes:any[] = [];
		Utils.forEachOwnProperty( NS.XSD.DataType, ( key:string, value:any ):void => {
			if ( URI.Util.isAbsolute( key ) ) {
				xsdDataTypes.push( {
					title: value,
					description: NS.XSD.DataType[ value ]
				} );
			}
		} );
		return xsdDataTypes;
	}

	validateSelectionInput( control:AbstractControl ):any {
		if ( ! control.value ) return { "emptySelectionError": true };
		return null;
	}
}
export interface PropertyType {
	name:string,
	value:string
}