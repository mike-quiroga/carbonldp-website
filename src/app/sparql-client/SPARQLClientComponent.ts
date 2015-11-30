import {
	Component, View,
	CORE_DIRECTIVES, FORM_DIRECTIVES,
	ElementRef,NgStyle
} from 'angular2/angular2';
import $ from 'jquery';
import 'semantic-ui/semantic';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import {SPARQLResponse, SPARQLResponseType} from './response/responseView';
import {Resultset, SPARQLFormats} from "./resultset/resultset";
import "./style.css!";


@Component( {
	selector: 'sparql-client',
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES, CodeMirrorComponent.Class, SPARQLResponse, NgStyle ]
} )
export default class SPARQLClientComponent {
	static parameters = [ [ ElementRef ] ];


	element:ElementRef;
	$element:JQuery;


	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	private _sparql:string = "Hello World";
	get sparql():string { return this._sparql; }

	set sparql( value:string ) {
		this._sparql = value;
		this.currentQuery.content = value;
		this.sparqlChanged();
	}


	SPARQLTypes:SPARQLTypes = <SPARQLTypes>{
		query: "Query",
		update: "Update"
	};
	//SPARQLQueryOperations:SPARQLQueryOperations = <SPARQLQueryOperations>{
	//	select: <SPARQLQueryOperation>{
	//		name: 'SELECT',
	//		formats: [
	//			{value: "table", name: "Friendly Table"},
	//			{value: "xml", name: "XML"},
	//			{value: "csv", name: "CSV"},
	//			{value: "tsv", name: "TSV"}
	//		]
	//	},
	//	describe: <SPARQLQueryOperation>{
	//		name: 'DESCRIBE',
	//		formats: [
	//			{value: "json-ld", name: "JSON-LD"},
	//			{value: "turtle", name: "TURTLE"},
	//			{value: "json-rdf", name: "RDF/JSON"},
	//			{value: "xml", name: "RDF/XML"},
	//			{value: "n3", name: "N3"}
	//		]
	//	},
	//	construct: <SPARQLQueryOperation>{
	//		name: 'CONSTRUCT',
	//		formats: [
	//			{value: "json-ld", name: "JSON-LD"},
	//			{value: "turtle", name: "TURTLE"},
	//			{value: "json-rdf", name: "RDF/JSON"},
	//			{value: "xml", name: "RDF/XML"},
	//			{value: "n3", name: "N3"}
	//		]
	//	},
	//	ask: <SPARQLQueryOperation>{
	//		name: 'ASK',
	//		formats: [
	//			{value: "boolean", name: "Boolean"}
	//		]
	//	},
	//};
	SPARQLQueryOperations:SPARQLQueryOperations = <SPARQLQueryOperations>{
		select: <SPARQLQueryOperation>{
			name: 'SELECT',
			formats: [
				{value: SPARQLFormats.table, name: "Friendly Table"},
				{value: SPARQLFormats.xml, name: "XML"},
				{value: SPARQLFormats.csv, name: "CSV"},
				{value: SPARQLFormats.tsv, name: "TSV"}
			]
		},
		describe: <SPARQLQueryOperation>{
			name: 'DESCRIBE',
			formats: [
				{value: SPARQLFormats.json_ld, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.json_rdf, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfxml, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"}
			]
		},
		construct: <SPARQLQueryOperation>{
			name: 'CONSTRUCT',
			formats: [
				{value: SPARQLFormats.json_ld, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.json_rdf, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfxml, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"}
			]
		},
		ask: <SPARQLQueryOperation>{
			name: 'ASK',
			formats: [
				{value: SPARQLFormats.bool, name: "Boolean"}
			]
		},
	};

	isQueryType:boolean;
	isQueryType:boolean = true;
	isSending:boolean = false;
	isSaving:boolean = false;
	isSavingMessage:string = "";
	responses:SPARQLClientResponse[] = [];


	currentQuery:SPARQLQuery = <SPARQLQuery>{
		endpoint: "",
		type: this.SPARQLTypes.query,
		content: "",
		operation: null,
		format: null,
		name: ""
	};
	//currentQueryId:Number;
	formatsAvailable = [];
	savedQueries:SPARQLQuery[] = this.getLocalSavedQueries() || [];
	sidebar:JQuery;
	//Buttons
	btnValidate:JQuery;
	btnExecute:JQuery;
	btnsGroupSaveQuery:JQuery;
	btnSaveQuery:JQuery;
	btnSave:JQuery;
	btnSaveAs:JQuery;


	//TODO: Remove this variable
	dummyDataForTable:any = {
		"head": {
			"vars": [ "context", "subject", "predicate", "object" ]
		},
		"results": {
			"bindings": [
				{
					"context": {"type": "uri", "value": "http://example.org/book/book6"},
					"subject": {"type": "uri", "value": "http://example.org/book/book6"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book6"},
					"object": {"type": "literal", "value": "Harry Potter and the Half-Blood Prince"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book6"},
					"subject": {"type": "uri", "value": "http://example.org/book/book6#fragment"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book6#fragment"},
					"object": {"type": "uri", "value": "http://example.org/book/book7"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book7"},
					"subject": {"type": "uri", "value": "http://example.org/book/book7"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book7"},
					"object": {"type": "literal", "value": "Harry Potter and the Deathly Hallows"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book5"},
					"subject": {"type": "uri", "value": "http://example.org/book/book5"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book5"},
					"object": {"type": "literal", "value": "Harry Potter and the Order of the Phoenix"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book4"},
					"subject": {"type": "uri", "value": "http://example.org/book/book4"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book4"},
					"object": {"type": "literal", "value": "1000", "datatype": "http://www.w3.org/2001/XMLSchema#integer"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book2"},
					"subject": {"type": "uri", "value": "http://example.org/book/book2"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book2"},
					"object": {"type": "literal", "value": "Harry Potter and the Chamber of Secrets"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book3"},
					"subject": {"type": "uri", "value": "http://example.org/book/book3"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book3"},
					"object": {"type": "literal", "value": "Harry Potter and the Prisoner Of Azkaban"}
				},
				{
					"context": {"type": "uri", "value": "http://example.org/book/book1"},
					"subject": {"type": "uri", "value": "http://example.org/book/book1"},
					"predicate": {"type": "uri", "value": "http://example.org/book/book1"},
					"object": {"type": "literal", "value": "Harry Potter and the Philosopher's Stone"}
				}
			]
		}
	};

	constructor( element:ElementRef ) {
		this.element = element;
		this.isSending = false;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.btnSaveQuery = this.$element.find( ".btnSaveQuery " );
		this.btnsGroupSaveQuery = this.$element.find( ".btnsGroupSaveQuery " );
		this.btnSave = this.btnsGroupSaveQuery.find( ".btnSave " );
		this.btnSaveAs = this.btnsGroupSaveQuery.find( ".btnSaveAs" );
		this.sidebar = this.$element.find( ".query-builder .ui.sidebar" );
		this.btnsGroupSaveQuery.find( ".dropdown" ).dropdown();
		this.initializeSavedQueriesSidebar();
	}


	//:JQueryEventObject
	onChangeQueryType( $event ):void {
		let type:string = $event.target.value;
		this.isQueryType = type == "Query";
	}


	/**
	 * Updates the currentQuery and the available formats depending on the SPARQL Query Operation
	 * Triggered whenever the user writes code inside the CodeMirror text area.
	 */
	sparqlChanged():void {
		let operation:string = this.getSPARQLOperation( this.sparql );
		if ( operation !== null && this.SPARQLQueryOperations[ operation.toLocaleLowerCase() ] ) {
			if ( operation == this.currentQuery.operation ) return;
			operation = operation.toLowerCase();
			this.currentQuery.format = this.currentQuery.format ? this.currentQuery.format : this.SPARQLQueryOperations[ operation ].formats[ 0 ].value;
			this.currentQuery.operation = operation;
			this.formatsAvailable = this.SPARQLQueryOperations[ operation ].formats;
		} else {
			this.currentQuery.format = null;
			this.currentQuery.operation = "update";
			this.formatsAvailable = [];
		}
	}

	/**
	 * Identifies which SPARL Query Operation will be called
	 * @param query  String. The content of the Code Mirror plugin.
	 * @returns      String. The name of the main SPARQL Query Operation.
	 */
	getSPARQLOperation( query:string ):string {
		let regExpModel:string = "((.|\n)+)?";
		switch ( true ) {
			case (new RegExp( regExpModel + "SELECT" + regExpModel, "i" )).test( query ):
				return this.SPARQLQueryOperations.select.name;
			case (new RegExp( regExpModel + "CONSTRUCT" + regExpModel, "i" )).test( query ):
				return this.SPARQLQueryOperations.construct.name;
			case (new RegExp( regExpModel + "ASK" + regExpModel, "i" )).test( query ):
				return this.SPARQLQueryOperations.ask.name;
			case (new RegExp( regExpModel + "DESCRIBE" + regExpModel, "i" )).test( query ):
				return this.SPARQLQueryOperations.describe.name;
			default:
				return null;
		}
	}

	isEmpty( value:any ):boolean {
		return (value == "" || value == null);
	};

	onExecute():void {
		this.isSending = true;
		let endpointURL:string = this.currentQuery.endpoint;
		let sparqlQuery:string = this.currentQuery.content;
		let format:string = this.currentQuery.format;
		/*
			var endpointURL = $scope.app.getURI() + query.endpoint;
		*/
		let promise:Promise = null;
		switch ( this.currentQuery.type ) {
			case this.SPARQLTypes.query:
				promise = this.executeQuery( endpointURL, sparqlQuery, format );
				break;
			case this.SPARQLTypes.update:
				promise = this.executeUpdate( endpointURL, sparqlQuery );
				break;
			default:
				// Unsupported Operation
				promise = new Promise( ( resolve:()=>string, reject:( msg:string )=>string )=> {
					reject( "Unsupported Type" );
				} );
		}

		promise.then(
			( response ) => {
				//console.log( "Carbon Response Succes: %o", response );
				this.isSending = false;
			},
			( response ) => {
				//console.log( "Carbon Response Fail: %o", response );
				this.isSending = false;
			}
		);
	}

	executeQuery( endpointURL:string, sparqlQuery:string, format:string ):Promise {
		this.isSending = true;
		if ( ! ! this.currentQuery.operation ) {
			this.currentQuery.operation = this.currentQuery.operation.toUpperCase();
		}
		switch ( this.currentQuery.operation ) {
			case this.SPARQLQueryOperations.select.name:
				return this.executeSelect( endpointURL, sparqlQuery, format );
			case this.SPARQLQueryOperations.describe.name:
				return this.executeModelQuery( endpointURL, sparqlQuery, format );
			case this.SPARQLQueryOperations.construct.name:
				return this.executeModelQuery( endpointURL, sparqlQuery, format );
			case this.SPARQLQueryOperations.ask.name:
				return this.executeAsk( endpointURL, sparqlQuery, format );
			default:
				// Unsupported Operation
				return new Promise( ( resolve:()=>string, reject:( msg:string )=>string )=> {
					reject( "Unsupported Operation" );
				} );
		}
	}

	executeSelect( endpointURL:string, sparqlQuery:string, format:string ):Promise {
		return new Promise(
			( resolve:( response:any )=>any, reject:( str:string )=>void )=> {
				let beforeTimestamp:Number = (new Date()).valueOf();
				let resultset = {
					"head": {
						"vars": [ "context", "subject", "predicate", "object" ]
					},
					"results": {
						"bindings": [
							{
								"context": {"type": "uri", "value": "http://example.org/book/book6"},
								"subject": {"type": "uri", "value": "http://example.org/book/book6"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book6"},
								"object": {"type": "literal", "value": "Harry Potter and the Half-Blood Prince"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book6"},
								"subject": {"type": "uri", "value": "http://example.org/book/book6#fragment"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book6#fragment"},
								"object": {"type": "uri", "value": "http://example.org/book/book7"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book7"},
								"subject": {"type": "uri", "value": "http://example.org/book/book7"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book7"},
								"object": {"type": "literal", "value": "Harry Potter and the Deathly Hallows"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book5"},
								"subject": {"type": "uri", "value": "http://example.org/book/book5"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book5"},
								"object": {"type": "literal", "value": "Harry Potter and the Order of the Phoenix"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book4"},
								"subject": {"type": "uri", "value": "http://example.org/book/book4"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book4"},
								"object": {"type": "literal", "value": "1000", "datatype": "http://www.w3.org/2001/XMLSchema#integer"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book2"},
								"subject": {"type": "uri", "value": "http://example.org/book/book2"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book2"},
								"object": {"type": "literal", "value": "Harry Potter and the Chamber of Secrets"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book3"},
								"subject": {"type": "uri", "value": "http://example.org/book/book3"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book3"},
								"object": {"type": "literal", "value": "Harry Potter and the Prisoner Of Azkaban"}
							},
							{
								"context": {"type": "uri", "value": "http://example.org/book/book1"},
								"subject": {"type": "uri", "value": "http://example.org/book/book1"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book1"},
								"object": {"type": "literal", "value": "Harry Potter and the Philosopher's Stone"}
							}
						]
					}
				};
				let afterTimestamp:Number = (new Date()).valueOf();
				var duration:number = afterTimestamp - beforeTimestamp;

				let response = new SPARQLClientResponse();
				response.sparql = sparqlQuery;
				response.endpointURL = endpointURL;
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.type = <string>SPARQLResponseType.success;
				response.operation = this.getSPARQLOperation( sparqlQuery );
				response.format = format;

				this.responses.push( response );
				resolve( response );
			}
		);
	}

	executeModelQuery( endpointURL:string, sparqlQuery:string, format:string ):Promise {
		return new Promise(
			( resolve:( response:any )=>any, reject:( str:string )=>void )=> {
				let beforeTimestamp:Number = (new Date()).valueOf();
				let resultset = {
					"head": {
						"vars": [ "context", "subject", "predicate", "object" ]
					},
					"results": {
						"bindings": [
							{
								"context": {"type": "uri", "value": "http://example.org/book/book6"},
								"subject": {"type": "uri", "value": "http://example.org/book/book6"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book6"},
								"object": {"type": "literal", "value": "Harry Potter and the Half-Blood Prince"}
							},
						]
					}
				};
				let afterTimestamp:Number = (new Date()).valueOf();
				var duration:number = afterTimestamp - beforeTimestamp;

				let response = new SPARQLClientResponse();
				response.sparql = sparqlQuery;
				response.endpointURL = endpointURL;
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.type = <string>SPARQLResponseType.success;
				response.operation = this.getSPARQLOperation( sparqlQuery );
				response.format = format;

				this.responses.push( response );
				resolve( response );
			}
		);
	}

	executeAsk( endpointURL:string, sparqlQuery:string, format:string ):Promise {
		return new Promise(
			( resolve:( response:any )=>any, reject:( str:string )=>void )=> {
				let beforeTimestamp:Number = (new Date()).valueOf();
				let resultset = {
					"head": {
						"vars": [ "context", "subject", "predicate", "object" ]
					},
					"results": {
						"bindings": [
							{
								"context": {"type": "uri", "value": "http://example.org/book/book6"},
								"subject": {"type": "uri", "value": "http://example.org/book/book6"},
								"predicate": {"type": "uri", "value": "http://example.org/book/book6"},
								"object": {"type": "literal", "value": "Harry Potter and the Half-Blood Prince"}
							},
						]
					}
				};
				let afterTimestamp:Number = (new Date()).valueOf();
				var duration:number = afterTimestamp - beforeTimestamp;

				let response = new SPARQLClientResponse();
				response.sparql = sparqlQuery;
				response.endpointURL = endpointURL;
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.type = <string>SPARQLResponseType.success;
				response.operation = this.getSPARQLOperation( sparqlQuery );
				response.format = format;

				this.responses.push( response );
				resolve( response );
			}
		);
	}

	executeUpdate( endpointURL:string, sparqlQuery:string ):Promise {
		return new Promise();
	}

	canExecute():boolean {
		return ! ! (this.currentQuery.endpoint && this.currentQuery.type && this.currentQuery.content && this.currentQuery.operation && this.currentQuery.format);

	}

	canSaveQuery():boolean {
		return ! ! (this.currentQuery.endpoint && this.currentQuery.type && this.currentQuery.content && this.currentQuery.operation && this.currentQuery.format && this.currentQuery.name);

	}

	onEmptyStack():void {
		this.responses = [];
	}

	onClickSaveQuery():void {
		let query:SPARQLQuery = <SPARQLQuery>{
			endpoint: this.currentQuery.endpoint,
			type: this.currentQuery.type,
			content: this.currentQuery.content,
			operation: this.currentQuery.operation,
			format: this.currentQuery.format,
			name: this.currentQuery.name,
			id: this.savedQueries.length
		};
		this.savedQueries = this.getLocalSavedQueries();
		this.savedQueries.push( query );
		this.updateLocalSavedQueries();
		//console.log( this.currentQuery );
		//this.sidebar.sidebar( 'toggle' );
		this.isSaving = true;
		this.isSavingMessage = "Saving...";
		setInterval( () => {
			this.isSaving = false;
			this.isSavingMessage = "";
		}, 500 );
	}

	onClickUpdate():void {
		this.savedQueries = this.getLocalSavedQueries();
		let queryIdx:Number = - 1;
		this.savedQueries.forEach( ( iteratingQuery:SPARQLQuery, index:Number )=> {
			if ( iteratingQuery.id == this.currentQuery.id ) {
				queryIdx = index;
			}
		} );
		if ( queryIdx > - 1 ) {
			this.savedQueries[ queryIdx ] = <SPARQLQuery>{
				endpoint: this.currentQuery.endpoint,
				type: this.currentQuery.type,
				content: this.currentQuery.content,
				operation: this.currentQuery.operation,
				format: this.currentQuery.format,
				name: this.currentQuery.name,
				id: this.currentQuery.id
			};
		} else {
			this.currentQuery.id = this.savedQueries.length;
			this.savedQueries.push( this.currentQuery );
		}
		//console.log( this.currentQuery );
		this.updateLocalSavedQueries();
	}

	onClickQuery( index:number ) {
		this.currentQuery = this.savedQueries[ index ];
		let format = this.currentQuery.format;
		this.sparql = this.currentQuery.content;
		//console.log( format );
		this.currentQuery.format = format;
		//console.log( this.currentQuery );
		//this.currentQueryId = this.currentQuery.id;

		this.initializeSavedQueriesSidebar();
	}

	onClickRemoveQuery( index:number ):void {
		this.savedQueries = this.getLocalSavedQueries();
		this.savedQueries.splice( index, 1 );
		this.updateLocalSavedQueries();
	}

	initializeSavedQueriesSidebar():void {
		this.sidebar.sidebar( {
				context: this.$element.find( '.query-builder .middle.segment' )
			} )
			.sidebar( 'attach events', '.query-builder .top.menu .item' );
	}

	getLocalSavedQueries():SPARQLQuery[] {
		return <SPARQLQuery[]>JSON.parse( window.localStorage.getItem( "savedQueries" ) );
	}

	updateLocalSavedQueries():void {
		window.localStorage.setItem( "savedQueries", JSON.stringify( this.savedQueries ) );
	}

	//returnSomethingDependingOnArgument<T>( argument:T ):T {
	//	let something:number = this.returnSomethingDependingOnArgument( "" );
	//
	//	return null;
	//}


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
export interface SPARQLQueryOperationFormat {
	name:string;
	value:string;
}
export interface SPARQLQueryOperation {
	name:string;
	formats:SPARQLQueryOperationFormat[];
}
export interface SPARQLQueryOperations {
	select:SPARQLQueryOperation;
	describe:SPARQLQueryOperation;
	construct:SPARQLQueryOperation;
	ask:SPARQLQueryOperation;
}
export interface SPARQLTypes {
	query:string;
	update:string;
}

export class SPARQLClientResponse {
	operation:string = null;
	sparql:string = null;
	endpointURL:string = null;
	duration:number = null;
	resultset:any = null;
	data:string = null;
	type:string = null;
	format:string = null;

	setData( data ):void {
		this.data = JSON.stringify( data, null, 2 );
	}
}