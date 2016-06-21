import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as Literal from "carbonldp/RDF/Literal";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";
import * as NS from "carbonldp/NS";


import LiteralValueComponent from "./literal-value/LiteralValueComponent";
import LiteralTypeComponent from "./literal-type/LiteralTypeComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "tr.literal",
	template: template,
	directives: [ LiteralValueComponent, LiteralTypeComponent ],
} )

export default class LiteralComponent {

	mode:string = Modes.READ;
	modes:Modes = Modes;
	tokens:string[] = [ "@value", "@type", "@language" ];
	saveAll:EventEmitter<boolean> = new EventEmitter<boolean>();
	isValidValue:boolean = false;
	isValidType:boolean = false;

	@Input() literal:Literal;
	private tempLiteral:any = {};

	constructor() {}


	displayEditor( event:Event ):void {
		this.mode = Modes.EDIT;
	}

	cancelEdit():void {
		this.mode = Modes.READ;
	}

	save():void {

	}

	changeValue( value:string|number|boolean ):void {
		this.tempLiteral[ "@value" ] = value;
	}

	changeType( type:{name:string, value:string} ):void {
		this.tempLiteral[ "@type" ] = type.value;
		// if(type===NS.XSD.DataType.string)
	}

	onIsValidValue( isValid:boolean ):void {
		this.isValidValue = isValid;
	}

	onIsValidType( isValid:boolean ):void {
		this.isValidType = isValid;
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
