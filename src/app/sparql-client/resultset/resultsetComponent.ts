import {
	Component, Input, Output,
	CORE_DIRECTIVES,
	ElementRef
} from 'angular2/angular2';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import {ResultsetTableComponent} from "../resultset-table/resultsetTableComponent";
import $ from 'jquery';
import 'semantic-ui/semantic';

import template from "./template.html!";

@Component( {
	selector: 'resultset',
	directives: [ CORE_DIRECTIVES, CodeMirrorComponent.Class, ResultsetTableComponent ],
	template: template
} )

export class ResultsetComponent {
	static parameters = [ [ ElementRef ] ];
	element:ElementRef;
	$element:JQuery;


	@Input() resultset:any;
	@Input() outputformat:string;

	SPARQLFormats:SPARQLFormats = SPARQLFormats;
	value:string = "Hola Mundo";


	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( element:ElementRef ) {
		this.element = element;
	}

	onInit():void {
		let format:string = this.getCodeMirrorMode( this.outputformat );
		this.outputformat = ! ! format ? format : this.outputformat;
		this.value = this.outputformat;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
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


	prefixURI( uri:string ):void {

		//for ( var prefix in Carbon.DefaultPrefixes ) {
		//	if ( Carbon.DefaultPrefixes.hasOwnProperty( prefix ) ) {
		//		var namespace = Carbon.DefaultPrefixes[prefix];
		//		if ( uri.lastIndexOf( namespace, 0 ) === 0 ) {
		//			return uri.replace( namespace, '<abbr title=&quot;' + namespace + '&quot;>' + prefix + '</abbr>:' );
		//		}
		//	}
		//}
	}
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