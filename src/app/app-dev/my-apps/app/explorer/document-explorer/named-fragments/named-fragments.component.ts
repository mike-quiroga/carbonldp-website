import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import NamedFragmentComponent from "./named-fragment.component"
import { NamedFragmentRecords } from "./named-fragment.component"
import PropertyComponent from "./../property/property.component";

import template from "./named-fragments.component.html!";
import style from "./named-fragments.component.css!text";

@Component( {
	selector: "document-named-fragments",
	template: template,
	styles: [ style ],
	directives: [ PropertyComponent, NamedFragmentComponent ],
} )

export default class NamedFragmentsComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedNamedFragments:RDFNode.Class[] = [];
	namedFragmentsChanges:Map<string, NamedFragmentRecords> = new Map<string, NamedFragmentRecords>();

	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() documentURI:string = "";

	@Output() onChanges:EventEmitter<Map<string, NamedFragmentRecords>> = new EventEmitter<Map<string, NamedFragmentRecords>>();
	@Output() onOpenBNode:EventEmitter<string> = new EventEmitter<string>();
	@Output() onOpenNamedFragment:EventEmitter<string> = new EventEmitter<string>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterContentInit():void {
		this.$element = $( this.element.nativeElement );
		this.nodesTab = this.$element.find( ".tabular.namedfragments.menu" ).tab();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( ( changes[ "namedFragments" ].currentValue !== changes[ "namedFragments" ].previousValue ) ) {
			this.openedNamedFragments = [];
			this.goToNamedFragment( "all-namedFragments" );
		}
	}

	getPropertiesName( property:any ):string[] {
		return Object.keys( property );
	}

	notifyNamedFragmentHasChanged( records:NamedFragmentRecords, namedFragment:RDFNode.Class ) {
		if ( typeof records === "undefined" || records === null ) {
			this.namedFragmentsChanges.delete( namedFragment[ "@id" ] );
			this.onChanges.emit( this.namedFragmentsChanges );
			return;
		}
		if ( records.changes.size > 0 || records.additions.size > 0 || records.deletions.size > 0 ) {
			this.namedFragmentsChanges.set( namedFragment[ "@id" ], records );
		} else {
			this.namedFragmentsChanges.delete( namedFragment[ "@id" ] );
		}
		this.onChanges.emit( this.namedFragmentsChanges );
	}

	openNamedFragment( nodeOrId:RDFNode.Class|string ):void {
		let node:RDFNode.Class;
		if ( typeof nodeOrId === "string" ) {
			node = this.namedFragments.find( ( node )=> { return node[ "@id" ] === nodeOrId} );
		} else {
			node = nodeOrId;
		}
		if ( this.openedNamedFragments.indexOf( node ) === - 1 )this.openedNamedFragments.push( node );
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

	closeNamedFragment( namedFragment:RDFNode.Class ):void {
		let idx:number = this.openedNamedFragments.indexOf( namedFragment );
		this.openedNamedFragments.splice( idx, 1 );
		this.goToNamedFragment( "all-namedFragments" );
		if ( this.namedFragmentsChanges.has( namedFragment[ "@id" ] ) )this.notifyNamedFragmentHasChanged( null, namedFragment );
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
