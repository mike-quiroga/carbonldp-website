import { Component, ElementRef, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import template from "./template.html!";

@Component( {
	selector: "property-list",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )

export default class ListViewerComponent {

	element:ElementRef;
	@Input() list:RDFNode.Class[];
	@Output() onGoTobNode:EventEmitter<string> = new EventEmitter<string>();

	headers:string[] = [];

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.headers = this.getHeaders();
	}

	ngAfterViewInit():void {}

	hasCommonHeaders():boolean {
		return this.headers.indexOf( "@id" ) > - 1 ? true : this.headers.indexOf( "@type" ) > - 1 ? true : this.headers.indexOf( "@value" ) > - 1 ? true : false;
	}

	hasHeader( value:string ):boolean {
		return this.headers.indexOf( value ) > - 1 ? true : false;
	}

	getHeaders():string[] {
		let temp:string[] = [];
		this.list.forEach(
			( item )=> {
				temp = temp.concat( Object.keys( item ) );
			}
		);
		return temp.filter(
			( item, pos ) => {
				return temp.indexOf( item ) == pos
			}
		);
	}

	goTobNode( id:string ):void {
		this.onGoTobNode.emit( id );
	}

	isbNode( uri:string ):boolean {
		return ! ! uri ? URI.Util.isBNodeID( uri ) : false;
	}

}
