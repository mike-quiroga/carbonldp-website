import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange, ViewChild } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";
import * as NS from "carbonldp/NS";
import { Error as HTTPError } from "carbonldp/HTTP/Errors";

import DocumentsResolverService from "./../DocumentsResolverService";

import DocumentResourceViewerComponent from "./../document-resource-viewer/DocumentResourceViewer";
import BNodesViewerComponent from "./../bnodes-viewer/BNodesViewerComponent";
import NamedFragmentsViewerComponent from "./../named-fragments-viewer/NamedFragmentsViewerComponent";
import PropertyComponent from "./../property/PropertyComponent";
// import PropertySingleValueComponent from "./../property-single-value/PropertySingleValueComponent";
import { Property } from "./../property/PropertyComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-viewer",
	host: { "[class.ui]": "true", "[class.basic]": "true", "[class.segment]": "true", },
	template: template,
	directives: [ CORE_DIRECTIVES, DocumentResourceViewerComponent, BNodesViewerComponent, NamedFragmentsViewerComponent, PropertyComponent ],
} )

export default class DocumentViewerComponent {
	element:ElementRef;
	$element:JQuery;
	sections:string[] = [ "bNodes", "namedFragments", "documentResource" ];

	rootNode:RDFNode.Class;

	bNodesArray:RDFNode.Class[] = [];
	namedFragmentsArray:RDFNode.Class[] = [];
	bNodesDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();
	namedFragmentsDictionary:Map<string,RDFNode.Class> = new Map<string,RDFNode.Class>();

	documentsResolverService:DocumentsResolverService;
	@Input() uri:string;
	@Input() documentContext:SDKContext.Class;

	private _document:RDFDocument.Class;
	get document():RDFDocument.Class {return this._document;}

	@Input() set document( value:RDFDocument.Class ) {
		this._document = value;
		this.receiveDocument( value );
	}


	@ViewChild( BNodesViewerComponent ) documentBNodes:BNodesViewerComponent;
	@ViewChild( NamedFragmentsViewerComponent ) namedFragments:NamedFragmentsViewerComponent;
	@Output() onLoadingDocument:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onSavingDocument:EventEmitter<boolean> = new EventEmitter<boolean>();

	propertyKind:{ SINGLE:string, MULTI:string, OBJECT:string} = { SINGLE: "single", MULTI: "multi", OBJECT: "object" };
	selectedPropertyKind:string;
	documentContentHasChanged:boolean = false;
	private _savingDocument:boolean = false;
	set savingDocument( value:boolean ) {
		this._savingDocument = value;
		this.onSavingDocument.emit( value );
	}

	get savingDocument():boolean { return this._savingDocument; }

	private _loadingDocument:boolean = false;
	set loadingDocument( value:boolean ) {
		this._loadingDocument = value;
		this.onLoadingDocument.emit( value );
	}

	get loadingDocument():boolean { return this._loadingDocument; }

	records:DocumentRecords = new DocumentRecords();

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
			this.getDocument( this.uri, this.documentContext ).then( ( document:RDFDocument.Class ) => {
				this.document = document[ 0 ];
			} );
		}
	}

	receiveDocument( document:RDFDocument.Class ):void {
		if ( ! ! document ) {
			console.log( "whole document has changed! %o: ", document );
			this.loadingDocument = true;
			this.records.additions.clear();
			this.records.changes.clear();
			this.records.deletions.clear();
			this.setRoot();
			this.generateMaps();
			this.loadingDocument = false;
			setTimeout(
				()=> {
					this.goToSection( "documentResource" );
					this.initializeTabs();
				}, 250
			);
		}
	}

	setRoot():void {
		this.rootNode = RDFDocument.Util.getDocumentResources( this.document )[ 0 ];
	}

	getDocument( uri:string, documentContext:SDKContext.Class ):Promise<RDFDocument.Class> {
		return this.documentsResolverService.get( uri, documentContext );
	}

	generateMaps():void {
		this.bNodesArray = RDFDocument.Util.getBNodeResources( this.document );
		this.namedFragmentsArray = RDFDocument.Util.getFragmentResources( this.document );
		this.bNodesDictionary.clear();
		this.namedFragmentsDictionary.clear();
		this.bNodesArray.forEach( ( node:RDFNode.Class ) => this.bNodesDictionary.set( node[ "@id" ], node ) );
		this.namedFragmentsArray.forEach( ( node:RDFNode.Class ) => this.namedFragmentsDictionary.set( node[ "@id" ], node ) );
	}

	openBNode( id:string ):void {
		this.documentBNodes.openBNode( id );
		this.goToSection( "bNodes" );
	}

	openNamedFragment( id:string ):void {
		this.namedFragments.openNamedFragment( id );
		this.goToSection( "namedFragments" );
	}

	initializeTabs() {
		this.$element.find( ".secondary.menu.document.tabs .item" ).tab();
	}

	onDisplayAddNewPropertyModal():void {
		this.selectedPropertyKind = this.propertyKind.SINGLE;
		this.$element.find( ".ui.radio.checkbox" ).checkbox();
	}

	changePropertyKind( kind:string ):void {
		this.selectedPropertyKind = kind;
	}


	goToSection( section:string ):void {
		if ( this.sections.indexOf( section ) === - 1 ) return;
		this.scrollTo( ">div:first-child" );
		this.$element.find( ".secondary.menu.document.tabs .item" ).tab( "changeTab", section );
	}

	changeProperty( property:Property ) {
		if ( this.records.changes.has( property.id ) ) {
			let prop:Property = this.records.changes.get( property.id );
			if ( prop.name === property.name && this.areEquals( property.value, prop.value ) ) {
				this.records.changes.delete( property.id );
			} else {
				this.records.changes.set( property.id, prop );
			}
		} else {
			if ( ! ! this.rootNode[ property.id ] && ! this.areEquals( property.value, this.rootNode[ property.id ] ) ) {
				let originalProperty:Property = <Property>{
					id: property.id,
					name: property.name,
					value: this.rootNode[ property.id ]
				};
				this.records.changes.set( property.id, originalProperty );
			}
		}
		this.rootNode[ property.id ] = property.value;
		this.document[ "@graph" ] = [ this.rootNode ];
		this.documentContentHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
	}

	private areEquals( object1:any, object2:any ):boolean {
		return JSON.stringify( object1 ) === JSON.stringify( object2 );
	}

	saveDocument():void {
		this.savingDocument = true;
		let body:string = JSON.stringify( this.document, null, "\t" );
		this.documentsResolverService.update( this.document[ "@id" ], body, this.documentContext ).then(
			( updatedDocument:RDFDocument.Class )=> {
				this.document = updatedDocument[ 0 ];
				this.records.changes.clear();
				this.records.additions.clear();
				this.records.deletions.clear();
			},
			( error:HTTPError )=> {console.error( error )}
		).then( ()=> {
			this.savingDocument = false;
			this.documentContentHasChanged = this.records.changes.size > 0 || this.records.additions.size > 0 || this.records.deletions.size > 0;
		} );
	}

	private scrollTo( selector:string ):void {
		if ( ! this.$element ) return;
		let divPosition:JQueryCoordinates = this.$element.find( selector ).position();
		if ( ! divPosition ) return;
		this.$element.animate( { scrollTop: divPosition.top }, "fast" );
	}
}

class DocumentRecords {
	changes:Map<string,Property> = new Map<string, Property>();
	deletions:Map<string,Property> = new Map<string, Property>();
	additions:Map<string,Property> = new Map<string, Property>();
}
