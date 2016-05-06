import { Component, ElementRef, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-bnodes",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent ],
} )

export default class BNodesViewerComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedbNodes:RDFNode.Class[] = [];
	@Input() bNodesArray:RDFNode.Class[] = [];
	@Input() bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	@Output() onOpenbNode:EventEmitter<string> = new EventEmitter<string>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.nodesTab = this.$element.find( ".tabular.bnodes.menu" ).tab();
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	openbNode( nodeOrId:RDFNode.Class|string ):void {
		let idx:number;
		let node:RDFNode.Class;
		if ( typeof nodeOrId === "string" ) {
			node = this.bNodesDictionary.get( nodeOrId );
		} else {
			node = nodeOrId;
		}
		idx = this.openedbNodes.indexOf( node );
		if ( idx === - 1 )this.openedbNodes.push( node );
		setTimeout( () => {
			this.refreshTabs();
			this.goTobNode( "bnode" + node[ "@id" ] );
		}, 50 );
	}

	goTobNode( id:string ) {
		this.nodesTab.find( "> [data-tab='" + id + "']" ).click();
		this.onOpenbNode.emit( "bNodes" );
	}

	closebNode( node:RDFNode.Class ):void {
		let idx:number = this.openedbNodes.indexOf( node );
		this.openedbNodes.splice( idx, 1 );
		this.goTobNode( "first" );
	}

	refreshTabs():void {
		this.nodesTab.find( ">.item" ).tab();
	}
}
