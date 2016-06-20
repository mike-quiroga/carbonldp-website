import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";
import * as NS from "carbonldp/NS";


import PropertyValuecomponent from "./../../property-value/PropertyValuecomponent";
import PropertyTypesComponent from "./../../property-types/PropertyTypesComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "tr.property-literal",
	template: template,
	directives: [ PropertyValuecomponent, PropertyTypesComponent ],
} )

export default class PropertyLiteralComponent {

	mode:string = Modes.READ;
	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	saveAll:EventEmitter<boolean> = new EventEmitter<boolean>();

	@Input() literal:Literal;
	private tempLiteral:Literal = <Literal>{};

	constructor() {}


	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		this.mode = Modes.READ;
	}

	save():void {
		console.log( "saving" );
		tempLiteral.value = type.value;
	}

	changeValue( value:string|number|boolean ):void {
		this.tempLiteral.value = value;
	}

	changeType( type:{name:string, value:string} ):void {
		this.tempLiteral.type = type.value;
	}

}
class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}
export interface Literal {
	value:string|number|boolean;
	type:string;
	language:string;
}
