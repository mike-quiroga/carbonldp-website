import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import PropertyComponent from "./../property/PropertyComponent";
import DocumentResourceViewer from "./../document-resource-viewer/DocumentResourceViewer"

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-named-fragments",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent, DocumentResourceViewer ],
} )

export default class NamedFragmentsViewerComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedNamedFragments:RDFNode.Class[] = [];
	@Input() documentURI:string;
	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.nodesTab = this.$element.find( ".tabular.namedfragments.menu" ).tab();
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	openNamedFragment( nodeOrId:RDFNode.Class|string ):void {
		let idx:number;
		let node:RDFNode.Class;
		if ( typeof nodeOrId === "string" ) {
			node = this.namedFragments.find( ( node )=> { return node[ "@id" ] === nodeOrId} );
		} else {
			node = nodeOrId;
		}
		idx = this.openedNamedFragments.indexOf( node );
		if ( idx === - 1 )this.openedNamedFragments.push( node );
		setTimeout( () => {
			this.refreshTabs();
			this.goToNamedFragment( "namedfragment_" + this.getNormalizedUri( node[ "@id" ] ) );
		}, 50 );
	}

	openBNode( id:string ):void {
		this.onOpenBNode.emit( id );
	}

	goToNamedFragment( id:string ) {
		if ( ! this.nodesTab )
			return;
		this.nodesTab.find( "> [data-tab='" + id + "']" ).click();
		this.onOpenNamedFragment.emit( "namedFragments" );
	}

	closeNamedFragment( node:RDFNode.Class ):void {
		let idx:number = this.openedNamedFragments.indexOf( node );
		this.openedNamedFragments.splice( idx, 1 );
		this.goToNamedFragment( "allNamedFragments" );
	}

	refreshTabs():void {
		this.nodesTab.find( ">.item" ).tab();
	}

	getNormalizedUri( uri:string ):string {
		return uri.replace( /[^\w\s]/gi, "" );
	}

	getSlug( uri:string ) {
		return URI.Util.getSlug( uri );
	}

}
