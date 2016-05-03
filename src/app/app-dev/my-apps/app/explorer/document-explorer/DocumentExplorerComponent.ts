import { Component, ElementRef, Input } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";


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
	@Input() documentContext:SDKContext.Class;

	loadingDocument:boolean = false;
	inspectingDocument:RDFDocument.Class;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		console.log( "Explorer: %o", this.documentContext );
		this.$element = $( this.element.nativeElement );
	}

	onLoadingDocument( loadingDocument:boolean ):void {
		this.loadingDocument = loadingDocument;
	}

	onSelectingDocument( document:RDFDocument.Class ):void {
		this.inspectingDocument = document;
	}
}
