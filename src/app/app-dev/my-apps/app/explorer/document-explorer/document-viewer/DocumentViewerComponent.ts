import { Component, ElementRef, Input, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as RDFDocument from "carbonldp/RDF/Document";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";

import BNodesViewerComponent from "./../bnodes-viewer/BNodesViewerComponent";
import TableListComponent from "./../table-list/TableListComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-viewer",
	template: template,
	directives: [ CORE_DIRECTIVES, BNodesViewerComponent, PropertyComponent, TableListComponent ],
} )

export default class DocumentViewerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;

	@Input() document:RDFDocument.Class;

	rootNode:RDFNode.Class;

	bNodesArray:RDFNode.Class[] = [];
	namedFragmentsArray:RDFNode.Class[] = [];

	bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngAfterViewInit():void {
		console.log( "Viewer: %o", this.document );
		this.$element = $( this.element.nativeElement );
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

	scrollTo( id:string ):void {
		if ( id === "bNodes" ) {
			let divPosition:JQueryCoordinates = this.$element.find( ".row.bNodes" ).position();
			this.$element.animate( { scrollTop: divPosition.top }, "fast" );
			// this.$element.parent().animate( { scrollTop: 0 }, "fast" );
		}
	}
}
