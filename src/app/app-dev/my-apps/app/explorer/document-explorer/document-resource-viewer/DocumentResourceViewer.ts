import { Component, ElementRef, Input, } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
// import "./style.css!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent ],
} )

export default class DocumentResourceComponent {

	element:ElementRef;
	$element:JQuery;

	@Input() rootNode:RDFNode.Class;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

}
