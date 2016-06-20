import { Component, ElementRef, ViewEncapsulation, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { Control, AbstractControl, Validators } from '@angular/common';

import $ from "jquery";
import "semantic-ui/semantic";

import * as NS from "carbonldp/NS";
import * as Utils from "carbonldp/Utils";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import PropertyLiteralComponent from "./property-literal/PropertyLiteralComponent";
import PropertyValuecomponent from "./../property-value/PropertyValuecomponent";
import PropertyTypesComponent from "./../property-types/PropertyTypesComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "property-literals",
	template: template,
	encapsulation: ViewEncapsulation.Emulated,
	directives: [ PropertyValuecomponent, PropertyTypesComponent, PropertyLiteralComponent ],
} )

export default class PropertyLiteralsComponent {

	tokens:string[] = [ "@value", "@type", "@language" ];
	@Input() literals:RDFNode.Class[] = [];

	constructor() {}


}