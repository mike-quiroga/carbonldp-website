import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";

import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";

import DocumentsResolverService from "./../document-explorer/DocumentsResolverService"
import DocumentViewerComponent from "./document-viewer/DocumentViewerComponent";
import DocumentTreeViewComponent from "./document-treeview/DocumentTreeViewComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-explorer",
	template: template,
	directives: [ CORE_DIRECTIVES, DocumentTreeViewComponent, DocumentViewerComponent ],
} )

export default class DocumentExplorerComponent {

	element:ElementRef;
	$element:JQuery;

	loadingDocument:boolean = false;
	inspectingDocument:RDFDocument.Class;
	inspectingUri:string;
	documentsResolverService:DocumentsResolverService;

	@Input() documentContext:SDKContext.Class;

	constructor( element:ElementRef, documentsResolverService:DocumentsResolverService ) {
		this.element = element;
		this.documentsResolverService = documentsResolverService;
	}

	ngAfterViewInit():void {
		console.log( "Explorer: %o", this.documentContext );
		this.$element = $( this.element.nativeElement );
	}

	onLoadingDocument( loadingDocument:boolean ):void {
		this.loadingDocument = loadingDocument;
	}

	resolveDocument( uri:string ):void {
		this.loadingDocument = true;
		let getDocument:Promise<RDFDocument.Class> = this.documentsResolverService.get( uri, this.documentContext );
		getDocument.then(
			( document:RDFDocument.Class )=> {
				// console.log( "Returned document: %o", document );
				this.inspectingDocument = document;
			}
		);
		getDocument.then(
			()=> {
				this.loadingDocument = false;
			}
		);
	}
}
