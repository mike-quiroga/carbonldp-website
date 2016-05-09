import { Component, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent ],
} )

export default class DocumentResourceComponent {

	@Input() rootNode:RDFNode.Class;
	@Output() onOpenbNode:EventEmitter<string> = new EventEmitter<string>();

	constructor() {}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	openbNode( id:string ):void {
		this.onOpenbNode.emit( id );
	}

}
