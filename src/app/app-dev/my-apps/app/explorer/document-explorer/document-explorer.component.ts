import { Component, ElementRef, Input } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";
import * as HTTP from "carbonldp/HTTP";

import DocumentsResolverService from "./documents-resolver.service.ts"
import DocumentViewerComponent from "./document-viewer/document-viewer.component";
import DocumentTreeViewComponent from "./document-tree-view/document-tree-view.component";

import { Message } from "carbon-panel/errors-area/error-message.component";

import template from "./document-explorer.component.html!";
import style from "./document-explorer.component.css!text";

@Component( {
	selector: "document-explorer",
	template: template,
	styles: [ style ],
	directives: [ DocumentTreeViewComponent, DocumentViewerComponent ],
} )

export default class DocumentExplorerComponent {

	element:ElementRef;
	$element:JQuery;

	loadingDocument:boolean = false;
	savingDocument:boolean = false;
	inspectingDocument:RDFDocument.Class;
	documentsResolverService:DocumentsResolverService;
	messages:Message[] = [];

	@Input() documentContext:SDKContext.Class;

	constructor( element:ElementRef, documentsResolverService:DocumentsResolverService ) {
		this.element = element;
		this.documentsResolverService = documentsResolverService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	onLoadingDocument( loadingDocument:boolean ):void {
		this.loadingDocument = loadingDocument;
	}

	showLoading( savingDocument:boolean ):void {
		this.savingDocument = savingDocument;
	}

	resolveDocument( uri:string ):void {
		this.loadingDocument = true;
		this.documentsResolverService.get( uri, this.documentContext ).then( ( document:RDFDocument.Class )=> {
			this.inspectingDocument = document[ 0 ];
			this.loadingDocument = false;
		} );
	}

	handleError( error:HTTP.Errors.Error ):void {
		let message:Message = {
			title: error.name,
			content: (<XMLHttpRequest>error.response.request).statusText,
			statusCode: "" + error.response.status,
			statusMessage: (<XMLHttpRequest>error.response.request).statusText,
			endpoint: (<any>error.response.request).responseURL,
		};
		this.messages.push( message );
	}
}
