import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import LiteralComponent from "./literal/LiteralComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "literals",
	template: template,
	encapsulation: ViewEncapsulation.Emulated,
	directives: [ LiteralComponent ],
} )

export default class LiteralsComponent {

	tokens:string[] = [ "@value", "@type", "@language" ];
	isLanguagePresent:boolean = false;
	isEditingLiteral:boolean = false;
	@Input() literals:RDFNode.Class[] = [];

	constructor() {}

	ngOnInit():void {
		this.isLanguagePresent = this.existsToken( "@language" );
	}

	existsToken( token:string ):boolean {
		let flag:boolean = false;
		this.literals.forEach( ( literal:any )=> {
			if ( ! ! literal[ token ] ) flag = true;
		} );
		return flag;
	}

	editModeChanged( value:boolean ):void {
		this.isEditingLiteral = value;
	}

	saveLiteral( literal:any ) {
		console.log( literal );
	}
}