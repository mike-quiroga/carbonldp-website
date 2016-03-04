/// <reference path="./../../../typings/typings.d.ts" />
import { Component, View, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES, FORM_DIRECTIVES, NgStyle } from "angular2/common";

import { ResponseComponent, SPARQLResponseType, SPARQLFormats, SPARQLClientResponse, SPARQLQuery } from './response/ResponseComponent';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import "./style.css!";


@Component( {
	selector: 'sparql-client',
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES, CodeMirrorComponent.Class, ResponseComponent, NgStyle, ResponseComponent ]
} )
export default class SPARQLClientComponent {
	static parameters = [ [ ElementRef ] ];


	element:ElementRef;
	$element:JQuery;


	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	private _sparql:string = "";
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
	SPARQLQueryOperations:SPARQLQueryOperations = <SPARQLQueryOperations>{
		select: {
			name: 'SELECT',
			formats: [
				{value: SPARQLFormats.table, name: "Friendly Table"},
				{value: SPARQLFormats.xml, name: "XML"},
				{value: SPARQLFormats.csv, name: "CSV"},
				{value: SPARQLFormats.tsv, name: "TSV"}
			]
		},
		describe: {
			name: 'DESCRIBE',
			formats: [
				{value: SPARQLFormats.jsonLD, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.jsonRDF, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfXML, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"}
			]
		},
		construct: {
			name: 'CONSTRUCT',
			formats: [
				{value: SPARQLFormats.jsonLD, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.jsonRDF, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfXML, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"}
			]
		},
		ask: {
			name: 'ASK',
			formats: [
				{value: SPARQLFormats.boolean, name: "Boolean"}
			]
		}
	};

	isQueryType:boolean;
	isQueryType:boolean = true;
	isSending:boolean = false;
	isSaving:boolean = false;
	responses:SPARQLClientResponse[] = [];


	currentQuery:SPARQLQuery = <SPARQLQuery>{
		endpoint: "",
		type: this.SPARQLTypes.query,
		content: "",
		operation: null,
		format: null,
		name: ""
	};
	formatsAvailable = [];
	savedQueries:SPARQLQuery[] = [];
	sidebar:JQuery;

	//Buttons
	btnsGroupSaveQuery:JQuery;
	btnSaveQuery:JQuery;
	btnSave:JQuery;
	btnSaveAs:JQuery;

	regExpSelect:RegExp = new RegExp( "((.|\n)+)?SELECT((.|\n)+)?", "i" );
	regExpConstruct:RegExp = new RegExp( "((.|\n)+)?CONSTRUCT((.|\n)+)?", "i" );
	regExpAsk:RegExp = new RegExp( "((.|\n)+)?ASK((.|\n)+)?", "i" );
	regExpDescribe:RegExp = new RegExp( "((.|\n)+)?DESCRIBE((.|\n)+)?", "i" );

	constructor( element:ElementRef ) {
		this.element = element;
		this.isSending = false;
		this.savedQueries = this.getLocalSavedQueries() || [];
	}

	ngAfterViewInit():void {
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
		this.isQueryType = type === "Query";
	}


	/**
	 * Updates the currentQuery and the available formats depending on the SPARQL Query Operation
	 * Triggered whenever the user writes code inside the CodeMirror text area.
	 */
	sparqlChanged():void {
		let operation:string = this.getSPARQLOperation( this.sparql );
		if ( operation !== null && this.SPARQLQueryOperations[ operation.toLowerCase() ] ) {
			operation = operation.toLowerCase();
			this.currentQuery.format = this.currentQuery.format ? this.currentQuery.format : this.SPARQLQueryOperations[ operation ].formats[ 0 ].value;
			this.currentQuery.operation = operation.toUpperCase();
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
			case (this.regExpSelect.test( query )):
				return this.SPARQLQueryOperations.select.name;
			case (this.regExpConstruct.test( query )):
				return this.SPARQLQueryOperations.construct.name;
			case (this.regExpAsk.test( query )):
				return this.SPARQLQueryOperations.ask.name;
			case (this.regExpDescribe.test( query )):
				return this.SPARQLQueryOperations.describe.name;
			default:
				return null;
		}
	}

	onReExecute( originalResponse:SPARQLClientResponse ):void {
		originalResponse.isReExecuting = true;
		this.execute( originalResponse.query, originalResponse ).then(
			( newResponse:SPARQLClientResponse ) => {
				originalResponse.isReExecuting = false;
				originalResponse.duration = newResponse.duration;
				originalResponse.resultset = newResponse.resultset;
				originalResponse.query = newResponse.query;
				originalResponse.data = newResponse.data;
			}
		);
	}

	onExecute():void {
		this.isSending = true;
		let query:SPARQLQuery = <SPARQLQuery>{
			endpoint: this.currentQuery.endpoint,
			type: this.currentQuery.type,
			content: this.currentQuery.content,
			operation: this.currentQuery.operation.toUpperCase(),
			format: this.currentQuery.format,
			name: this.currentQuery.name,
			id: null
		};

		this.execute( query, null ).then(
			( response ) => {
				this.addResponse( response );
				return response;
			}
		);
	}

	execute( query:SPARQLQuery, activeResponse?:SPARQLClientResponse ):Promise<SPARQLClientResponse> {
		/*
			var endpointURL = $scope.app.getURI() + query.endpoint;
		*/

		let type = query.type;
		if ( activeResponse ) {
			query = activeResponse.query;
		}
		let promise:Promise = null;
		switch ( type ) {
			case this.SPARQLTypes.query:
				promise = this.executeQuery( query );
				break;
			case this.SPARQLTypes.update:
				promise = this.executeUpdate( query );
				break;
			default:
				// Unsupported Operation
				promise = new Promise( ( resolve:()=>string, reject:( msg:string )=>string )=> {
					reject( "Unsupported Type" );
				} );
		}

		return promise.then(
			( response ) => {
				// Carbon Response Success
				this.isSending = false;
				return response;
			},
			( error ) => {
				//Carbon Response Fail
				this.isSending = false;
				return error;
			}
		);
	}

	executeQuery( query:SPARQLQuery ):Promise {
		this.isSending = true;
		switch ( query.operation ) {
			case this.SPARQLQueryOperations.select.name:
				return this.executeSelect( query );
			case this.SPARQLQueryOperations.describe.name:
				return this.executeModelQuery( query );
			case this.SPARQLQueryOperations.construct.name:
				return this.executeModelQuery( query );
			case this.SPARQLQueryOperations.ask.name:
				return this.executeAsk( query );
			default:
				// Unsupported Operation
				return new Promise( ( resolve:()=>string, reject:( msg:string )=>string )=> {
					reject( "Unsupported Operation" );
				} );
		}
	}

	executeSelect( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		return new Promise(
			( resolve:( response:any )=>SPARQLClientResponse, reject:( str:string )=>string )=> {
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
				let duration:number = afterTimestamp - beforeTimestamp;

				let random = Math.floor( Math.random() * 10 );
				if ( random % 2 == 0 ) {
					resultset = {
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
				}

				let response:SPARQLClientResponse = new SPARQLClientResponse();
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.result = <string>SPARQLResponseType.success;
				response.query = query;

				resolve( response );
			}
		);
	}

	executeModelQuery( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		return new Promise(
			( resolve:( response:any )=>SPARQLClientResponse, reject:( str:string )=>string )=> {
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
				let duration:number = afterTimestamp - beforeTimestamp;
				let random = Math.floor( Math.random() * 10 );
				if ( random % 2 == 0 ) {
					resultset = {
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
				}

				let response = new SPARQLClientResponse();
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.result = <string>SPARQLResponseType.success;
				response.query = query;

				resolve( response );
			}
		);
	}

	executeAsk( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		return new Promise(
			( resolve:( response:any )=>SPARQLClientResponse, reject:( str:string )=>string )=> {
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
				let duration:number = afterTimestamp - beforeTimestamp;
				let random = Math.floor( Math.random() * 10 );
				if ( random % 2 == 0 ) {
					resultset = {
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
				}

				let response = new SPARQLClientResponse();
				response.duration = duration;
				response.resultset = resultset;
				response.setData( resultset );
				response.result = <string>SPARQLResponseType.success;
				response.query = query;

				resolve( response );
			}
		);
	}

	executeUpdate( query:SPARQLQuery ):Promise {
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

	onRemove( response:any ):void {
		let idx:Number = this.responses.indexOf( response );
		if ( idx > - 1 )
			this.responses.splice( idx, 1 );
	}

	onConfigureResponse( response:SPARQLClientResponse ):void {
		this.currentQuery = response.query;
		this.sparql = response.query.content;
	}

	addResponse( response:SPARQLClientResponse ):void {
		let responsesLengh:number = this.responses.length, i:number;
		for ( i = responsesLengh; i > 0; i -- ) {
			this.responses[ i ] = this.responses[ i - 1 ];
		}
		this.responses[ 0 ] = response;
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
		this.isSaving = true;
		setInterval( () => {
			this.isSaving = false;
		}, 500 );
	}

	onClickSaveExistingQuery():void {
		this.savedQueries = this.getLocalSavedQueries();
		let queryIdx:Number = - 1;
		this.savedQueries.forEach( ( iteratingQuery:SPARQLQuery, index:Number )=> {
			if ( iteratingQuery.id === this.currentQuery.id ) {
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
		this.updateLocalSavedQueries();
	}

	onClickQuery( index:number ) {
		this.currentQuery = this.savedQueries[ index ];
		this.sparql = this.currentQuery.content;
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
		if ( ! window.localStorage.getItem( "savedQueries" ) )
			this.updateLocalSavedQueries();
		return <SPARQLQuery[]>JSON.parse( window.localStorage.getItem( "savedQueries" ) );
	}

	updateLocalSavedQueries():void {
		window.localStorage.setItem( "savedQueries", JSON.stringify( this.savedQueries ) );
	}


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