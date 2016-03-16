import {Component, Input, Output, ElementRef, EventEmitter, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, NgClass } from "angular2/common";

import SPARQLClientComponent from "./../SPARQLClientComponent";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import { ResultsetTableComponent } from "./../resultset-table/ResultsetTableComponent";
import $ from "jquery";
import "semantic-ui/semantic";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "sparql-response",
	directives: [ CORE_DIRECTIVES, NgClass, CodeMirrorComponent.Class, ResultsetTableComponent ],
	template: template,
} )
export class ResponseComponent {
	element:ElementRef;
	$element:JQuery;

	@Input() outputformat:string;
	@Input() response:SPARQLClientResponse;
	@Input() prefixes:{ [ prefix:string ]:string };

	@Output() onRemove:EventEmitter = new EventEmitter();
	@Output() onConfigure:EventEmitter = new EventEmitter();
	@Output() onReExecute:EventEmitter = new EventEmitter();

	SPARQLFormats:SPARQLFormats = SPARQLFormats;

	get codeMirrorMode():typeof CodeMirrorComponent.Mode { return CodeMirrorComponent.Mode; }

	accordion:any;
	accordionOpen:boolean = true;
	menu:any;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		this.outputformat = this.response.query.format;
		let format:string = this.getCodeMirrorMode( this.response.query.format );
		this.outputformat = ! ! format ? format : this.outputformat;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.accordion = this.$element.find( ".accordion" );
		this.accordion.accordion( {
			onOpen: this.onOpen.bind( this ),
		} );
		this.menu = this.$element.find( ".content .tabular.menu > .item" );
		this.menu.tab();

		this.openAccordion();
	}

	toggleAccordion():void {
		this.accordion.accordion( "toggle" );
		this.accordionOpen = ! this.accordionOpen;
	}

	openAccordion():void {
		this.accordion.accordion( "open", 0 );
		this.accordionOpen = true;
	}

	onRemoveResponse():void {
		this.onRemove.next( this.response );
	}

	onOpen():void {
		this.$element.find( ".CodeMirror" ).each( function ( i, element ) {
			element.CodeMirror.refresh();
		} );
	}

	onConfigureResponse():void {
		this.onConfigure.next( this.response );
	}

	onReExecuteResponse():void {
		this.accordion.accordion( "open", 0 );
		this.onReExecute.next( this.response );
	}

	getCodeMirrorMode( format:string ):string {
		switch ( format ) {
			case SPARQLFormats.csv:
				return CodeMirrorComponent.Mode.CSV;
			case SPARQLFormats.xml:
				return CodeMirrorComponent.Mode.XML;
			case SPARQLFormats.jsonLD:
				return CodeMirrorComponent.Mode.JSONLD;
			case SPARQLFormats.jsonRDF:
				return CodeMirrorComponent.Mode.JSONDRDF;
			case SPARQLFormats.n3:
				return CodeMirrorComponent.Mode.N3;
			case SPARQLFormats.rdfXML:
				return CodeMirrorComponent.Mode.RDFXML;
			case SPARQLFormats.tsv:
				return CodeMirrorComponent.Mode.TSV;
			case SPARQLFormats.turtle:
				return CodeMirrorComponent.Mode.TURTLE;
			case SPARQLFormats.boolean:
				return CodeMirrorComponent.Mode.JAVASCRIPT;
			default:
				return null;
		}
	}
}
export class SPARQLResponseType {
	static success:string = "success";
	static default:string = "default";
	static error:string = "error";
}

export class SPARQLFormats {
	static table:string = "table";
	static xml:string = "xml";
	static csv:string = "csv";
	static tsv:string = "tsv";
	static jsonLD:string = "json-ld";
	static turtle:string = "turtle";
	static jsonRDF:string = "json-rdf";
	static rdfXML:string = "rdfxml";
	static n3:string = "n3";
	static boolean:string = "boolean";
}

export interface SPARQLQuery {
	endpoint:string;
	type:string;
	content:string;
	operation:string;
	format:string;
	name:string;
	id:number;
}

export class SPARQLClientResponse {
	duration:number = null;
	resultset:any = null;
	query:SPARQLQuery = null;
	result:string = null;
	isReExecuting:boolean = false;
	data:string = null;

	setData( data ):void {
		this.data = JSON.stringify( data, null, 2 );
	}
}
