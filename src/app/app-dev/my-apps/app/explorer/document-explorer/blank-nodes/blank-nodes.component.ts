import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";

import BlankNodeComponent from "./blank-node.component"
import { BlankNodeRecords } from "./blank-node.component"
import PropertyComponent from "./../property/property.component";

import template from "./blank-nodes.component.html!";
import style from "./blank-nodes.component.css!text";

@Component( {
	selector: "document-bnodes",
	template: template,
	styles: [ style ],
	directives: [ PropertyComponent, BlankNodeComponent ],
} )

export default class BlankNodesComponent {

	element:ElementRef;
	$element:JQuery;

	nodesTab:JQuery;
	openedBNodes:RDFNode.Class[] = [];
	bNodesChanges:Map<string, BlankNodeRecords> = new Map<string, BlankNodeRecords>();

	@Input() bNodes:RDFNode.Class[] = [];
	@Input() namedFragments:RDFNode.Class[] = [];
	@Input() documentURI:string = "";

	@Output() onChanges:EventEmitter<Map<string, BlankNodeRecords>> = new EventEmitter<Map<string, BlankNodeRecords>>();
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

	notifyDocumentBNodeHasChanged( records:BlankNodeRecords, bNode:RDFNode.Class ) {
		if ( typeof records === "undefined" || records === null ) {
			this.bNodesChanges.delete( bNode[ "@id" ] );
			this.onChanges.emit( this.bNodesChanges );
			return;
		}
		if ( records.changes.size > 0 || records.additions.size > 0 || records.deletions.size > 0 ) {
			this.bNodesChanges.set( bNode[ "@id" ], records );
		} else {
			this.bNodesChanges.delete( bNode[ "@id" ] );
		}
		this.onChanges.emit( this.bNodesChanges );
	}

	openBNode( nodeOrId:RDFNode.Class|string ):void {
		let node:RDFNode.Class;
		if ( typeof nodeOrId === "string" ) {
			node = this.bNodes.find( ( node )=> { return node[ "@id" ] === nodeOrId} );
		} else {
			node = nodeOrId;
		}
		if ( this.openedBNodes.indexOf( node ) === - 1 ) this.openedBNodes.push( node );
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

	closeBNode( bNode:RDFNode.Class ):void {
		let idx:number = this.openedBNodes.indexOf( bNode );
		this.openedBNodes.splice( idx, 1 );
		this.goToBNode( "all" );
		if ( this.bNodesChanges.has( bNode[ "@id" ] ) )this.notifyDocumentBNodeHasChanged( null, bNode );
	}

	refreshTabs():void {
		this.nodesTab.find( ">.item" ).tab();
	}
}
