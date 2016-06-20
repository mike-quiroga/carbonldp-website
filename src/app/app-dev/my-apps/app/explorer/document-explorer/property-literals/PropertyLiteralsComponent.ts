import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import PropertyValuecomponent from "./../property-value/PropertyValuecomponent";
import PropertyTypesComponent from "./../property-types/PropertyTypesComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "property-literals",
	template: template,
	encapsulation: ViewEncapsulation.Emulated,
	directives: [ PropertyValuecomponent, PropertyTypesComponent ],
} )

export default class PropertyLiteralsComponent {

	mode:string = Modes.READ;
	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	@Input() literals:RDFNode.Class[] = [];

	constructor() {}


	hasToken( token:string ):boolean {
		return this.literals.filter( ( literal )=> {return ! ! literal[ token ]} ).length > 0;
	}

	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		this.mode = Modes.READ;
	}

	save():void {
		console.log( "saving" );
	}

}
class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}