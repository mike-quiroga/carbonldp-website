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
	@Input() literals:RDFNode.Class[] = [];

	constructor() {}

	existsToken():boolean {
		return false;
	}

}