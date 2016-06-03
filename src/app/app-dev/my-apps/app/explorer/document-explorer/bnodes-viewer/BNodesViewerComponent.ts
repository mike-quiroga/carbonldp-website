import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

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
	openedBNodes:RDFNode.Class[] = [];
	@Input() bNodesArray:RDFNode.Class[] = [];
	@Input() bNodesDictionary:Map<string, RDFNode.Class> = new Map<string, RDFNode.Class>();
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.nodesTab = this.$element.find( ".tabular.bnodes.menu" ).tab();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( ( changes[ "bNodesArray" ].currentValue !== changes[ "bNodesArray" ].previousValue ) ||
			( changes[ "bNodesDictionary" ].currentValue !== changes[ "bNodesDictionary" ].previousValue ) ) {
			this.openedBNodes = [];
			this.goToBNode( "all" );
		}
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	openBNode( nodeOrId:RDFNode.Class|string ):void {
		let idx:number;
		let node:RDFNode.Class;
		if ( typeof nodeOrId === "string" ) {
			node = this.bNodesDictionary.get( nodeOrId );
		} else {
			node = nodeOrId;
		}
		idx = this.openedBNodes.indexOf( node );
		if ( idx === - 1 ) this.openedBNodes.push( node );
		setTimeout( () => {
			this.refreshTabs();
			this.goToBNode( "bnode" + node[ "@id" ] );
		}, 50 );
	}

	goToBNode( id:string ) {
		if ( ! this.nodesTab ) return;
		this.nodesTab.find( "> [data-tab='" + id + "']" ).click();
		this.onOpenBNode.emit( "bNodes" );
	}

	closeBNode( node:RDFNode.Class ):void {
		let idx:number = this.openedBNodes.indexOf( node );
		this.openedBNodes.splice( idx, 1 );
		this.goToBNode( "all" );
	}

	refreshTabs():void {
		this.nodesTab.find( ">.item" ).tab();
	}
}
