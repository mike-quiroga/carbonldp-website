import { Component, ElementRef, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-named-fragments",
	template: template,
	directives: [ CORE_DIRECTIVES, PropertyComponent ],
} )

export default class NamedFragmentsViewerComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedNamedFragments:RDFNode.Class[] = [];
	@Input() documentURI:string;
	@Input() namedFragmentsArray:RDFNode.Class[] = [];
	@Input() namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
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
			node = this.namedFragmentsDictionary.get( nodeOrId );
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

	goToBNode( id:string ):void {
		this.onOpenBNode.emit( id );
	}

}
