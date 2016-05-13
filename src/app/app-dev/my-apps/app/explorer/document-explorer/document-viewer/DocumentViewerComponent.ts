import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange, ViewChild } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";

import DocumentsResolverService from "./../DocumentsResolverService";

import DocumentResourceViewerComponent from "./../document-resource-viewer/DocumentResourceViewer";
import BNodesViewerComponent from "./../bnodes-viewer/BNodesViewerComponent";
import NamedFragmentsViewerComponent from "./../named-fragments-viewer/NamedFragmentsViewerComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-viewer",
	template: template,
	directives: [ CORE_DIRECTIVES, DocumentResourceViewerComponent, BNodesViewerComponent, NamedFragmentsViewerComponent, PropertyComponent ],
} )

export default class DocumentViewerComponent {
	element:ElementRef;
	$element:JQuery;

	rootNode:RDFNode.Class;

	bNodesArray:RDFNode.Class[] = [];
	namedFragmentsArray:RDFNode.Class[] = [];

	bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();

	@Input() uri:string;
	@Input() document:RDFDocument.Class;
	@Input() documentContext:SDKContext.Class;
	@ViewChild( BNodesViewerComponent ) documentBNodes:BNodesViewerComponent;
	@ViewChild( NamedFragmentsViewerComponent ) namedFragments:NamedFragmentsViewerComponent;
	@Output() onLoadingDocument:EventEmitter<boolean> = new EventEmitter();

	set loadingDocument( value:boolean ) {
		this._loadingDocument = value;
		this.onLoadingDocument.emit( value );
	}

	get loadingDocument():boolean { return this._loadingDocument; }

	private _loadingDocument:boolean = false;

	documentsResolverService:DocumentsResolverService;

	constructor( element:ElementRef, documentsResolverService:DocumentsResolverService ) {
		this.element = element;
		this.documentsResolverService = documentsResolverService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "uri" ] && ! ! changes[ "uri" ].currentValue && changes[ "uri" ].currentValue !== changes[ "uri" ].previousValue ) {
			this.loadingDocument = true;
			this.getDocument( this.uri, this.documentContext ).then(
				( document:RDFDocument.Class ) => {
					this.document = document;
					this.receiveDocument();

					this.loadingDocument = false;
				}
			);
		}
		if ( changes[ "document" ] && ! ! changes[ "document" ].currentValue && changes[ "document" ].currentValue !== changes[ "document" ].previousValue ) {
			this.loadingDocument = true;
			this.receiveDocument();
			this.loadingDocument = false;
		}
	}

	receiveDocument():void {
		this.document = this.document[ 0 ];
		this.setRoot();
		this.generateMaps();
	}

	setRoot():void {
		this.rootNode = RDFDocument.Util.getDocumentResources( this.document )[ 0 ];
	}

	getDocument( uri:string, documentContext:SDKContext.Class ):Promise<RDFDocument.Class> {
		return this.documentsResolverService.get( uri, documentContext );
	}

	generateMaps():void {
		this.bNodesArray = [];
		this.namedFragmentsArray = [];
		this.bNodesDictionary.clear();
		this.namedFragmentsDictionary.clear();
		let nodes:RDFNode.Class[] = this.document[ "@graph" ];
		nodes.forEach( ( node:RDFNode.Class ) => {
			if ( URI.Util.isBNodeID( node[ "@id" ] ) ) {
				this.bNodesDictionary.set( node[ "@id" ], node );
				this.bNodesArray.push( node );
			}
			if ( URI.Util.hasFragment( node[ "@id" ] ) ) {
				this.namedFragmentsDictionary.set( node[ "@id" ], node );
				this.namedFragmentsArray.push( node );
			}
		} );
	}

	openBNode( id:string ):void {
		this.documentBNodes.openBNode( id );
		this.scrollTo( "bNodes" );
	}

	openNamedFragment( id:string ):void {
		this.namedFragments.openNamedFragment( id );
		this.scrollTo( "namedFragments" );
	}

	scrollTo( id:string ):void {
		if ( id === "bNodes" ) {
			let divPosition:JQueryCoordinates = this.$element.find( ".row.bNodes" ).position();
			this.$element.animate( { scrollTop: divPosition.top }, "fast" );
		}
		if ( id === "namedFragments" ) {
			let divPosition:JQueryCoordinates = this.$element.find( ".row.namedFragments" ).position();
			this.$element.animate( { scrollTop: divPosition.top }, "fast" );
		}
	}
}
