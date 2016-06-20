import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "property-literals",
	template: template,
	encapsulation: ViewEncapsulation.Emulated
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
		console.log( "Canceled" );
	}

	save():void {
		console.log( "saving" );
	}

}
export class Modes {
	static EDIT:string = "EDIT";
	static READ:string = "READ";
}