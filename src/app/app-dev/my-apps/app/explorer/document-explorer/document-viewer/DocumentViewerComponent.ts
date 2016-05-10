import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange, ViewChild } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFDocument from "carbonldp/RDF/Document";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";
import * as Pointer from "carbonldp/Pointer";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as Request from "carbonldp/HTTP/Request";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as NS from "carbonldp/NS";
import * as RDFDocument from "carbonldp/RDF/Document";

import DocumentResourceViewerComponent from "./../document-resource-viewer/DocumentResourceViewer";
import BNodesViewerComponent from "./../bnodes-viewer/BNodesViewerComponent";
import PropertyComponent from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-viewer",
	template: template,
	directives: [ CORE_DIRECTIVES, DocumentResourceViewerComponent, BNodesViewerComponent, PropertyComponent ],
} )

export default class DocumentViewerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;

	rootNode:RDFNode.Class;

	bNodesArray:RDFNode.Class[] = [];
	namedFragmentsArray:RDFNode.Class[] = [];

	bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();

	loadingDocument:boolean = false;
	@Input() uri:string;
	@Input() document:RDFDocument.Class;
	@Input() documentContext:SDKContext.Class;
	@ViewChild( BNodesViewerComponent ) documentbNodes:BNodesViewerComponent;
	@Output() onLoadingDocument:EventEmitter<boolean> = new EventEmitter();

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngOnInit():void {
		if ( this.document ) {
			this.setRoot();
			this.generateMaps();
		}
	}

	ngAfterViewInit():void {
		console.log( "Viewer: %o", this.document );
		this.$element = $( this.element.nativeElement );
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "uri" ] && changes[ "uri" ].currentValue !== changes[ "uri" ].previousValue ) {
			if ( ! changes[ "uri" ].currentValue )
				return;
			this.loadingDocument = true;
			this.onLoadingDocument.emit( this.loadingDocument );
			let requestOptions:Request.Options = {
				sendCredentialsOnCORS: true,
			};
			if ( this.documentContext && this.documentContext.auth.isAuthenticated() ) this.documentContext.auth.addAuthentication( requestOptions );
			let parser:RDFDocument.Parser = new RDFDocument.Parser();
			let resolveNode:Promise<HTTP.Response.Class> = this.resolveUri( this.uri, requestOptions );
			resolveNode.then(
				( response:HTTP.Response.Class ) => {
					console.log( "Returned uri: %o", response );
					parser.parse( response.data ).then(
						( parsedDocument:RDFDocument.Class ) => {
							console.log( "Parsed uri: %o", parsedDocument );
							if ( ! parsedDocument[ 0 ] )
								return;
							this.document = <RDFDocument.Class>parsedDocument[ 0 ];
							this.setRoot();
							this.generateMaps();
						}
					);
				}
			);
			resolveNode.then(
				()=> {
					this.loadingDocument = false;
					this.onLoadingDocument.emit( this.loadingDocument );
				}
			);
		}
		if ( changes[ "document" ] && changes[ "document" ].currentValue !== changes[ "document" ].previousValue ) {
			if ( ! changes[ "document" ].currentValue )
				return;
			this.setRoot();
			this.generateMaps();
		}
	}

	setRoot():void {
		console.log( this.document );
		this.rootNode = <RDFNode.Class>{};
		let documents:RDFNode.Class[] = RDFDocument.Util.getDocumentResources( this.document );
		console.log( documents );
		this.rootNode = documents[ 0 ];
		console.log( this.rootNode );
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
		console.log( "bNodes: %o - NamedFragments: %o", this.bNodesDictionary, this.namedFragmentsDictionary );
	}

	openbNode( id:string ):void {
		this.documentbNodes.openbNode( id );
		this.scrollTo( "bNodes" );
	}

	scrollTo( id:string ):void {
		if ( id === "bNodes" ) {
			let divPosition:JQueryCoordinates = this.$element.find( ".row.bNodes" ).position();
			this.$element.animate( { scrollTop: divPosition.top }, "fast" );
		}
	}

	resolveUri( uri:string, requestOptions:Request.Options ):Promise<HTTP.Response.Class> {
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.RDFSource, requestOptions );
		return HTTP.Request.Service.get( uri, requestOptions );
	}
}
