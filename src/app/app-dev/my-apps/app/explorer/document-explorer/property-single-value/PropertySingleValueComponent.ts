import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, ViewChild } from "@angular/core";
import { AbstractControl, Control, Validators } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";

import PropertyTypesComponent from "./../property-types/PropertyTypesComponent";
import { PropertyType } from "./../property-types/PropertyTypesComponent";
import PropertyValuecomponent from "./../property-value/PropertyValuecomponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "property-single-value",
	template: template,
	directives: [ PropertyTypesComponent, PropertyValuecomponent ],
	host: { "[class.equal]": "evenlyDivided", "[class.width]": "evenlyDivided" },
	encapsulation: ViewEncapsulation.Emulated
} )

export default class PropertySingleValueComponent {

	valueInput:AbstractControl;
	typeInput:AbstractControl;
	nameInput:AbstractControl;

	@Input() type:string = NS.XSD.DataType.string;
	@Input() submitButtonText:string;
	@Input() displaySubmitButton:boolean = true;
	@Input() evenlyDivided:boolean = true;
	@Output() onSubmit:EventEmitter<boolean> = new EventEmitter<boolean>();
	@ViewChild( PropertyTypesComponent ) propertyTypes:PropertyTypesComponent;
	@ViewChild( PropertyValuecomponent ) propertyValue:PropertyValuecomponent;


	constructor() { }

	ngAfterViewInit():void {
		this.typeInput = this.propertyTypes.input;
		this.valueInput = this.propertyValue.input;
	}

	ngOnInit():void {
		this.nameInput = new Control( "", Validators.compose( [ Validators.required, this.validateName.bind( this ) ] ) );
	}

	onSubmitButton():void {
		this.onSubmit.emit( true );
	}

	onTypeSelected( type:PropertyType ):void {
		this.type = type.value;
		this.typeInput = this.propertyTypes.input;
	}

	onValueChange( value:string ):void {
		this.valueInput = this.propertyValue.input;
	}

	isButtonDisabled():boolean {
		// return (! ! this.typeInput && ! ! this.valueInput) && (! this.typeInput.valid || ! this.valueInput.valid);
		return false;
	}

	submit():void {
		console.log( "Name: %o", this.nameInput.value );
		console.log( "Type: %o", this.typeInput.value );
		console.log( "Value: %o", this.valueInput.value );
		this.propertyTypes.input.markAsTouched();
		this.propertyValue.input.markAsTouched();
		this.nameInput.markAsTouched();
		this.onSubmit.emit( true );
	}

	ngAfterViewChecked():void {
		if ( this.propertyValue.input != this.valueInput ) {
			console.log( "Changed value" );
			this.valueInput = this.propertyValue.input;
		}
		if ( this.propertyTypes.input != this.typeInput ) {
			console.log( "Changed type" );
			this.typeInput = this.propertyTypes.input;
		}
	}

	private validateName( control:AbstractControl ):any {
		if ( ! control.value ) return { "emptyError": true };
		return null;
	}

}