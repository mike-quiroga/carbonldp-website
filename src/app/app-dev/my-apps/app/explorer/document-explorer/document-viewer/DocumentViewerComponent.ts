import { Component, ElementRef, Input, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES, NgSwitch, NgSwitchWhen, NgSwitchDefault } from "angular2/common";
import { Router } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as RDFDocument from "carbonldp/RDF/Document";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import TableListComponent from "./../table-list/TableListComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-viewer",
	template: template,
	directives: [ CORE_DIRECTIVES, NgSwitchDefault, PropertyComponent, TableListComponent ],
} )

export default class DocumentViewerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;

	bNodesSection:JQuery;
	nodesTab:JQuery;

	@Input() document:RDFDocument.Class;

	rootNode:RDFNode.Class;
	bNodesArray:RDFNode.Class[] = [];
	namedFragmentsArray:RDFNode.Class[] = [];
	bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	openedbNodes:RDFNode.Class[] = [];

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngAfterViewInit():void {
		console.log( "Viewer: %o", this.document );
		this.$element = $( this.element.nativeElement );
		this.bNodesSection = this.$element.find( "#bNodes" );
		this.nodesTab = this.bNodesSection.find( ".tabular.bnodes.menu" ).tab();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "document" ].currentValue !== changes[ "document" ].previousValue ) {
			console.log( this.document );
			this.rootNode = <RDFNode.Class>{}
			let documents:RDFNode.Class[] = RDFDocument.Util.getDocumentResources( this.document );
			console.log( documents );
			this.rootNode = documents[ 0 ];
			console.log( this.rootNode );
			this.generateMaps();
			console.log( "bNodes: %o - NamedFragments: %o", this.bNodesDictionary, this.namedFragmentsDictionary );
			console.log( "bNodes: %o - NamedFragments: %o", this.bNodesDictionary.size, this.namedFragmentsDictionary.size );
			console.log( this.bNodesDictionary.keys() );
		}
	}

	generateMaps():void {
		this.bNodesArray = [];
		this.namedFragmentsArray = [];
		this.bNodesDictionary = new Map<string,RDFNode.Class>();
		this.namedFragmentsDictionary = new Map<string,RDFNode.Class>();
		let nodes:RDFNode.Class[] = this.document[ "@graph" ];
		nodes.forEach(
			( node:RDFNode.Class ) => {
				if ( URI.Util.isBNodeID( node[ "@id" ] ) ) {
					this.bNodesDictionary.set( node[ "@id" ], node );
					this.bNodesArray.push( node );
				}
				if ( URI.Util.hasFragment( node[ "@id" ] ) ) {
					this.namedFragmentsDictionary.set( node[ "@id" ], node );
					this.namedFragmentsArray.push( node );
				}
			}
		);
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
			this.goToBNode( "bnode" + node[ "@id" ] );
		}, 50 );
	}

	goToBNode( id:string ) {
		this.nodesTab.find( "> [data-tab='" + id + "']" ).click();
		this.$element.parent().animate( { scrollTop: 0 }, "fast" );
	}

	closeBNode( node:RDFNode.Class ):void {
		let idx:number = this.openedbNodes.indexOf( node );
		this.openedbNodes.splice( idx, 1 );
		this.goToBNode( "first" );
	}

	refreshTabs():void {
		this.nodesTab.find( ">.item" ).tab();
	}

	getFriendlyId( id:string ):string {
		return id.replace( /\W/g, "" );
	}
}
