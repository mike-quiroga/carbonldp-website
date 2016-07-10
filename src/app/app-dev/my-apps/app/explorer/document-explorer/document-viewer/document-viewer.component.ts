import { Component, ElementRef, Input, Output, EventEmitter, SimpleChange, ViewChild } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import { Message } from "carbon-panel/errors-area/error-message.component";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";
import { JSONLDParser as JSONLDParser } from "carbonldp/HTTP";
import { Error as HTTPError } from "carbonldp/HTTP/Errors";

import DocumentsResolverService from "./../documents-resolver.service.ts";

import DocumentResourceComponent from "./../document-resource/document-resource.component";
import { RootRecords } from "./../document-resource/document-resource.component";
import BlankNodesComponent from "./../blank-nodes/blank-nodes.component";
import NamedFragmentsComponent from "./../named-fragments/named-fragments.component";
import PropertyComponent from "./../property/property.component";
import { BlankNodeRecords } from "./../blank-nodes/blank-node.component";
import { NamedFragmentRecords } from "./../named-fragments/named-fragment.component";

import template from "./document-viewer.component.html!";
import style from "./document-viewer.component.css!text";

@Component( {
	selector: "document-viewer",
	host: { "[class.ui]": "true", "[class.basic]": "true", "[class.segment]": "true", },
	template: template,
	styles: [ style ],
	directives: [ DocumentResourceComponent, BlankNodesComponent, NamedFragmentsComponent, PropertyComponent ],
} )

export default class DocumentViewerComponent {
	element:ElementRef;
	$element:JQuery;
	sections:string[] = [ "bNodes", "namedFragments", "documentResource" ];
	rootNode:RDFNode.Class;
	bNodes:RDFNode.Class[] = [];
	namedFragments:RDFNode.Class[] = [];
	savingErrorMessage:Message;

	rootNodeHasChanged:boolean = false;
	rootNodeRecords:RootRecords;
	bNodesHaveChanged:boolean = false;
	bNodesChanges:Map<string, BlankNodeRecords>;
	namedFragmentsHaveChanged:boolean = false;
	namedFragmentsChanges:Map<string, NamedFragmentRecords>;

	get documentContentHasChanged() {
		return this.rootNodeHasChanged || this.bNodesHaveChanged || this.namedFragmentsHaveChanged;
	}


	documentsResolverService:DocumentsResolverService;
	@Input() uri:string;
	@Input() documentContext:SDKContext.Class;
	private _document:RDFDocument.Class;
	@Input() set document( value:RDFDocument.Class ) {
		this._document = value;
		this.receiveDocument( value );
	}

	get document():RDFDocument.Class {return this._document;}

	@Output() onLoadingDocument:EventEmitter<boolean> = new EventEmitter<boolean>();
	@Output() onSavingDocument:EventEmitter<boolean> = new EventEmitter<boolean>();

	@ViewChild( BlankNodesComponent ) documentBNodes:BlankNodesComponent;
	@ViewChild( NamedFragmentsComponent ) documentNamedFragments:NamedFragmentsComponent;

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
			this.loadingDocument = true;
			this.setRoot();
			this.generateFragments();
			this.clearDocumentChanges();
			this.loadingDocument = false;
			this.savingErrorMessage = null;
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

	generateFragments():void {
		this.bNodes = RDFDocument.Util.getBNodeResources( this.document );
		this.namedFragments = RDFDocument.Util.getFragmentResources( this.document );
	}

	openBNode( id:string ):void {
		this.documentBNodes.openBNode( id );
		this.goToSection( "bNodes" );
	}

	openNamedFragment( id:string ):void {
		this.documentNamedFragments.openNamedFragment( id );
		this.goToSection( "namedFragments" );
	}

	initializeTabs() {
		this.$element.find( ".secondary.menu.document.tabs .item" ).tab();
	}

	goToSection( section:string ):void {
		if ( this.sections.indexOf( section ) === - 1 ) return;
		this.scrollTo( ">div:first-child" );
		this.$element.find( ".secondary.menu.document.tabs .item" ).tab( "changeTab", section );
	}

	registerRootNodeChanges( records:RootRecords ):void {
		this.rootNodeRecords = records;
		this.rootNodeHasChanged = records.changes.size > 0 || records.additions.size > 0 || records.deletions.size > 0;
	}

	registerBNodeChanges( bNodeChanges:Map<string, BlankNodeRecords> ):void {
		this.bNodesChanges = bNodeChanges;
		this.bNodesHaveChanged = bNodeChanges.size > 0;
	}

	registerNamedFragmentsChanges( namedFragmentsChanges:Map<string, NamedFragmentRecords> ):void {
		this.namedFragmentsChanges = namedFragmentsChanges;
		this.namedFragmentsHaveChanged = namedFragmentsChanges.size > 0;
	}

	modifyRootNodeWithChanges():void {
		if ( ! ! this.rootNodeRecords ) {
			this.rootNodeRecords.deletions.forEach( ( property, key )=> {
				delete this.rootNode[ key ];
			} );
			this.rootNodeRecords.changes.forEach( ( property, key )=> {
				if ( property.modified.id !== property.modified.name ) {
					delete this.rootNode[ key ];
					this.rootNode[ property.modified.name ] = property.modified.value;
				} else {
					this.rootNode[ key ] = property.modified.value;
				}
			} );
			this.rootNodeRecords.additions.forEach( ( property, key )=> {
				this.rootNode[ key ] = property.added.value;
			} );

		}
	}

	modifyBNodesWithChanges():void {
		let tempBNode;
		this.bNodesChanges.forEach( ( bNodeRecords:BlankNodeRecords, bNodeId:string )=> {
			tempBNode = this.bNodes.find( (bNode => {return bNode[ "@id" ] === bNodeId}) );
			bNodeRecords.deletions.forEach( ( property, key )=> {
				delete tempBNode[ key ];
			} );
			bNodeRecords.changes.forEach( ( property, key )=> {
				if ( property.modified.id !== property.modified.name ) {
					delete tempBNode[ key ];
					tempBNode[ property.modified.name ] = property.modified.value;
				} else {
					tempBNode[ key ] = property.modified.value;
				}
			} );
			bNodeRecords.additions.forEach( ( property, key )=> {
				tempBNode[ key ] = property.added.value;
			} );

		} );
	}

	modifyNamedFragmentsWithChanges():void {
		let tempNamedFragment;
		this.namedFragmentsChanges.forEach( ( namedFragmentRecords:NamedFragmentRecords, namedFragmentId:string )=> {
			tempNamedFragment = this.namedFragments.find( (namedFragment => {return namedFragment[ "@id" ] === namedFragmentId}) );
			namedFragmentRecords.deletions.forEach( ( property, key )=> {
				delete tempNamedFragment[ key ];
			} );
			namedFragmentRecords.changes.forEach( ( property, key )=> {
				if ( property.modified.id !== property.modified.name ) {
					delete tempNamedFragment[ key ];
					tempNamedFragment[ property.modified.name ] = property.modified.value;
				} else {
					tempNamedFragment[ key ] = property.modified.value;
				}
			} );
			namedFragmentRecords.additions.forEach( ( property, key )=> {
				tempNamedFragment[ key ] = property.added.value;
			} );

		} );
	}

	clearDocumentChanges():void {
		this.rootNodeRecords = new RootRecords();
		this.bNodesChanges = new Map<string, BlankNodeRecords>();
		this.namedFragmentsChanges = new Map<string, NamedFragmentRecords>();
		this.rootNodeHasChanged = false;
		this.bNodesHaveChanged = false;
		this.namedFragmentsHaveChanged = false;
	}

	saveDocument():void {
		this.savingDocument = true;
		this.modifyRootNodeWithChanges();
		this.modifyBNodesWithChanges();
		this.modifyNamedFragmentsWithChanges();
		let body:string = JSON.stringify( this.document, null, "\t" );
		this.documentsResolverService.update( this.document[ "@id" ], body, this.documentContext ).then(
			( updatedDocument:RDFDocument.Class )=> {
				this.document = updatedDocument[ 0 ];
			}
		).catch( ( error:HTTPError )=> {
			this.savingErrorMessage = {
				title: error.name,
				content: (<XMLHttpRequest>error.response.request).statusText,
				statusCode: "" + error.response.status,
				statusMessage: (<XMLHttpRequest>error.response.request).statusText,
				endpoint: (<any>error.response.request).responseURL,
			};
			if ( ! ! error.response.data ) {
				// TODO: Change this method to use the correct HTTPError when Javascript SDK implements it
				this.getErrors( error ).then( ( errors )=> {
					this.savingErrorMessage.errors = errors;
				} );
			}
		} ).then( ()=> {
			this.savingDocument = false;
			this.rootNodeHasChanged = this.rootNodeRecords.changes.size > 0 || this.rootNodeRecords.additions.size > 0 || this.rootNodeRecords.deletions.size > 0;
			this.bNodesHaveChanged = this.bNodesChanges.size > 0;
			this.namedFragmentsHaveChanged = this.namedFragmentsChanges.size > 0;
		} );
	}

	getErrors( error:HTTPError ):Promise<any[]> {
		let parser:JSONLDParser.Class = new JSONLDParser.Class();
		let mainError = {};
		let errors:any[] = [];
		return parser.parse( error.response.data ).then( ( mainErrors )=> {
			mainError = mainErrors.find( ( error )=> { return error[ "@type" ].indexOf( "https://carbonldp.com/ns/v1/platform#ErrorResponse" ) !== - 1} );
			errors = mainErrors.filter( ( error )=> { return error[ "@type" ].indexOf( "https://carbonldp.com/ns/v1/platform#Error" ) !== - 1} );
			return errors;
		} );
	}

	clearSavingError():void {
		this.savingErrorMessage = null;
	}

	private scrollTo( selector:string ):void {
		if ( ! this.$element ) return;
		let divPosition:JQueryCoordinates = this.$element.find( selector ).position();
		if ( ! divPosition ) return;
		this.$element.animate( { scrollTop: divPosition.top }, "fast" );
	}
}
