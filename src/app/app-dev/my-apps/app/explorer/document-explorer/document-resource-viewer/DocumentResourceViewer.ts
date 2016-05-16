import { Component, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";

@Component( {
	selector: "document-resource",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent ],
} )

export default class DocumentResourceComponent {

	@Input() displayOnly:string[] = [];
	@Input() hiddenProperties:string[] = [];
	@Input() rootNode:RDFNode.Class;
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();

	constructor() {}


	openBNode( id:string ):void {
		this.onOpenBNode.emit( id );
	}

	openNamedFragment( id:string ):void {
		this.onOpenNamedFragment.emit( id );
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	getDisplayName( uri:string ):string {
		if ( URI.Util.hasFragment( uri ) )return URI.Util.getFragment( uri );
		return URI.Util.getSlug( uri );
	}

	canDisplay( propertyName:string ):boolean {
		if ( typeof propertyName === "undefined" ) return false;
		if ( this.displayOnly.length === 0 && this.hiddenProperties.length === 0 ) return true;
		if ( this.displayOnly.length > 0 ) return this.displayOnly.indexOf( propertyName ) !== - 1 ? true : false;
		return this.hiddenProperties.indexOf( propertyName ) !== - 1 ? false : true;
	}
}
