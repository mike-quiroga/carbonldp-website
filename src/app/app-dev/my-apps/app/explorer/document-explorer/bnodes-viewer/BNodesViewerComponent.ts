import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/PropertyComponent";
import DocumentResourceViewer from "./../document-resource-viewer/DocumentResourceViewer"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-bnodes",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent, DocumentResourceViewer ],
} )

export default class BNodesViewerComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedBNodes:RDFNode.Class[] = [];
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() documentURI:string = "";
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.nodesTab = this.$element.find( ".tabular.bnodes.menu" ).tab();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( ( changes[ "bNodes" ].currentValue !== changes[ "bNodes" ].previousValue ) ) {
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
			node = this.bNodes.find( ( node )=> { return node[ "@id" ] === nodeOrId} );
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

	openNamedFragment( id:string ):void {
		this.onOpenNamedFragment.emit( id );
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
