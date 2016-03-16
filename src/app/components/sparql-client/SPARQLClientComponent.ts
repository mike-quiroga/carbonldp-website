import { Component, ElementRef, Injector, Input } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, NgStyle } from "angular2/common";
import {RouteConfig, RouterOutlet, CanActivate, Router} from 'angular2/router';

import { ResponseComponent, SPARQLResponseType, SPARQLFormats, SPARQLClientResponse, SPARQLQuery } from "./response/ResponseComponent";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import * as SPARQL from "carbon/SPARQL";
import * as HTTP from "carbon/HTTP";
import * as Credentials from "carbon/Auth/Credentials";

import { appInjector } from "app/boot";
import Cookies from "js-cookie";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";
import Carbon from "carbon/Carbon";
import Context from "carbon/Context";


@Component( {
	selector: "sparql-client",
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES, CodeMirrorComponent.Class, ResponseComponent, NgStyle, ResponseComponent ]
} )
@CanActivate(
	( prev, next ):boolean => {
		let injector:Injector = appInjector();
		let carbon:Carbon = injector.get( Carbon );
		let router:Router = injector.get( Router );

		if ( ! carbon ) {
			router.navigate( [ "/Website/Login" ] );
			return false;
		}
		// TODO: Change this to use a token instead of raw credentials when the SDK provides a way of authenticate using tokens.
		let cookiesHandler:Cookies = Cookies;
		let tokenCookie:{email:string, password:String} = cookiesHandler.getJSON( "carbon_jwt" );
		if ( tokenCookie && ! carbon.auth.isAuthenticated() ) {
			return carbon.auth.authenticate( tokenCookie.email, tokenCookie.password ).then(
				( credentials:Credentials ) => {
					return carbon.auth.isAuthenticated();
				}
			).catch(
				( error:Error ) => {
					switch ( true ) {
						case error instanceof HTTP.Errors.UnauthorizedError:
							console.log( "Wrong credentials" );
							break;
						default:
							console.log( "There was a problem processing the request" );
							break;
					}
					router.navigate( [ "/Website/Login" ] );
					return false;
				}
			);
		}
		if ( ! carbon.auth.isAuthenticated() )
			router.navigate( [ "/Website/Login" ] );
		return carbon.auth.isAuthenticated();
	}
)
export default class SPARQLClientComponent {
	get codeMirrorMode():typeof CodeMirrorComponent.Mode { return CodeMirrorComponent.Mode; }

	get sparql():string { return this._sparql; }

	set sparql( value:string ) {
		this._sparql = value;
		this.currentQuery.content = value;
		this.sparqlChanged();
	}

	get endpoint():string { return this._endpoint; }

	set endpoint( value:string ) {
		this._endpoint = value;
		this.endpointChanged();
	}

	private _endpoint:string = "";

	endpointChanged():void {
		if ( this.regExpURL.test( this.endpoint ) ) {
			this.currentQuery.endpoint = this.endpoint;
		} else {
			this.currentQuery.endpoint = this.context.base + this.endpoint;
		}
	}

	SPARQLTypes:SPARQLTypes = <SPARQLTypes>{
		query: "Query",
		update: "Update",
	};
	SPARQLQueryOperations:SPARQLQueryOperations = <SPARQLQueryOperations>{
		select: {
			name: "SELECT",
			formats: [
				{value: SPARQLFormats.table, name: "Friendly Table"},
				{value: SPARQLFormats.xml, name: "XML"},
				{value: SPARQLFormats.csv, name: "CSV"},
				{value: SPARQLFormats.tsv, name: "TSV"},
			],
		},
		describe: {
			name: "DESCRIBE",
			formats: [
				{value: SPARQLFormats.jsonLD, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.jsonRDF, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfXML, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"},
			],
		},
		construct: {
			name: "CONSTRUCT",
			formats: [
				{value: SPARQLFormats.jsonLD, name: "JSON-LD"},
				{value: SPARQLFormats.turtle, name: "TURTLE"},
				{value: SPARQLFormats.jsonRDF, name: "RDF/JSON"},
				{value: SPARQLFormats.rdfXML, name: "RDF/XML"},
				{value: SPARQLFormats.n3, name: "N3"},
			],
		},
		ask: {
			name: "ASK",
			formats: [
				{value: SPARQLFormats.boolean, name: "Boolean"},
			],
		},
	};

	isQueryType:boolean = true;
	isSending:boolean = false;
	isSaving:boolean = false;
	isCarbonContext:boolean = false;
	responses:SPARQLClientResponse[] = [];


	currentQuery:SPARQLQuery = <SPARQLQuery>{
		endpoint: "",
		type: this.SPARQLTypes.query,
		content: "",
		operation: null,
		format: null,
		name: "",
	};
	askingQuery:SPARQLQuery = <SPARQLQuery>{
		endpoint: "",
		type: this.SPARQLTypes.query,
		content: "",
		operation: null,
		format: null,
		name: "",
	};
	formatsAvailable = [];
	savedQueries:SPARQLQuery[] = [];
	sidebar:JQuery;
	confirmationModal:JQuery;

	// Buttons
	btnsGroupSaveQuery:JQuery;
	btnSaveQuery:JQuery;
	btnSave:JQuery;
	btnSaveAs:JQuery;

	regExpSelect:RegExp = new RegExp( "((.|\n)+)?SELECT((.|\n)+)?", "i" );
	regExpConstruct:RegExp = new RegExp( "((.|\n)+)?CONSTRUCT((.|\n)+)?", "i" );
	regExpAsk:RegExp = new RegExp( "((.|\n)+)?ASK((.|\n)+)?", "i" );
	regExpDescribe:RegExp = new RegExp( "((.|\n)+)?DESCRIBE((.|\n)+)?", "i" );
	regExpURL:RegExp = new RegExp( "(https?:\/\/(?:www\.|(?!www))[^\s\.]+\.[^\s]{2,}|www\.[^\s]+\.[^\s]{2,})" );

	@Input() context:Context;
	private element:ElementRef;

	// TODO: Make them configurable
	private prefixes:{ [ prefix:string ]:string } = {
		"acl": "http://www.w3.org/ns/auth/acl#",
		"api": "http://purl.org/linked-data/api/vocab#",
		"c": "https://carbonldp.com/ns/v1/platform#",
		"cs": "https://carbonldp.com/ns/v1/security#",
		"cp": "https://carbonldp.com/ns/v1/patch#",
		"cc": "http://creativecommons.org/ns#",
		"cert": "http://www.w3.org/ns/auth/cert#",
		"dbp": "http://dbpedia.org/property/",
		"dc": "http://purl.org/dc/terms/",
		"dc11": "http://purl.org/dc/elements/1.1/",
		"dcterms": "http://purl.org/dc/terms/",
		"doap": "http://usefulinc.com/ns/doap#",
		"example": "http://example.org/ns#",
		"ex": "http://example.org/ns#",
		"exif": "http://www.w3.org/2003/12/exif/ns#",
		"fn": "http://www.w3.org/2005/xpath-functions#",
		"foaf": "http://xmlns.com/foaf/0.1/",
		"geo": "http://www.w3.org/2003/01/geo/wgs84_pos#",
		"geonames": "http://www.geonames.org/ontology#",
		"gr": "http://purl.org/goodrelations/v1#",
		"http": "http://www.w3.org/2006/http#",
		"ldp": "http://www.w3.org/ns/ldp#",
		"log": "http://www.w3.org/2000/10/swap/log#",
		"owl": "http://www.w3.org/2002/07/owl#",
		"rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
		"rdfs": "http://www.w3.org/2000/01/rdf-schema#",
		"rei": "http://www.w3.org/2004/06/rei#",
		"rsa": "http://www.w3.org/ns/auth/rsa#",
		"rss": "http://purl.org/rss/1.0/",
		"sd": "http://www.w3.org/ns/sparql-service-description#",
		"sfn": "http://www.w3.org/ns/sparql#",
		"sioc": "http://rdfs.org/sioc/ns#",
		"skos": "http://www.w3.org/2004/02/skos/core#",
		"swrc": "http://swrc.ontoware.org/ontology#",
		"types": "http://rdfs.org/sioc/types#",
		"vcard": "http://www.w3.org/2001/vcard-rdf/3.0#",
		"wot": "http://xmlns.com/wot/0.1/",
		"xhtml": "http://www.w3.org/1999/xhtml#",
		"xsd": "http://www.w3.org/2001/XMLSchema#",
	};

	private $element:JQuery;
	private carbon:Carbon;
	private _sparql:string = "";

	constructor( element:ElementRef, carbon:Carbon ) {
		this.element = element;
		this.isSending = false;
		this.savedQueries = this.getLocalSavedQueries() || [];
		this.carbon = carbon;
	}

	ngOnInit():void {
		console.log( this.context );
		if ( ! this.context ) {
			this.context = this.carbon;
			this.isCarbonContext = true;
		} else {
			this.endpoint = this.context.base;
		}
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.btnSaveQuery = this.$element.find( ".btnSaveQuery" );
		this.btnsGroupSaveQuery = this.$element.find( ".btnsGroupSaveQuery" );
		this.btnSave = this.btnsGroupSaveQuery.find( ".btnSave" );
		this.btnSaveAs = this.btnsGroupSaveQuery.find( ".btnSaveAs" );
		this.sidebar = this.$element.find( ".query-builder .ui.sidebar" );
		this.btnsGroupSaveQuery.find( ".dropdown" ).dropdown();
		this.confirmationModal = this.$element.find( ".ui.replace-confirmation.modal" );
		this.initializeSavedQueriesSidebar();
		this.initializeModal();
	}

	onChangeQueryType( $event:JQueryEventObject ):void {
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
			id: null,
		};

		this.execute( query, null ).then(
			( response ) => {
				this.addResponse( response );
				return response;
			}
		);
	}

	execute( query:SPARQLQuery, activeResponse?:SPARQLClientResponse ):Promise<SPARQLClientResponse> {
		let type:string = query.type;
		if ( activeResponse ) {
			query = activeResponse.query;
		}
		let promise:Promise = null;
		switch ( type ) {
			case this.SPARQLTypes.query:
				promise = this.executeQuery( query );
				break;
			case this.SPARQLTypes.update:
				promise = this.executeUPDATE( query );
				break;
			default:
				// Unsupported Operation
				promise = new Promise( ( resolve:() => string, reject:( msg:string ) => string ) => {
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
				// Carbon Response Fail
				this.isSending = false;
				return error;
			}
		);
	}

	executeQuery( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		this.isSending = true;
		switch ( query.operation ) {
			case this.SPARQLQueryOperations.select.name:
				return this.executeSELECT( query );
			case this.SPARQLQueryOperations.describe.name:
				return this.executeDESCRIBE( query );
			case this.SPARQLQueryOperations.construct.name:
				return this.executeCONSTRUCT( query );
			case this.SPARQLQueryOperations.ask.name:
				return this.executeASK( query );
			default:
				// Unsupported Operation
				return new Promise( ( resolve:() => string, reject:( msg:string ) => string ) => {
					reject( "Unsupported Operation" );
				} );
		}
	}

	executeSELECT( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		let beforeTimestamp:number = ( new Date() ).valueOf();
		return this.context.documents.executeRawSELECTQuery( query.endpoint, query.content ).then(
			( [ result, response ]:[ SPARQL.RawResults.Class, HTTP.Response.Class ] ):SPARQLClientResponse => {
				let duration:number = (new Date()).valueOf() - beforeTimestamp;

				let clientResponse:SPARQLClientResponse = new SPARQLClientResponse();
				clientResponse.duration = duration;
				clientResponse.resultset = result;
				clientResponse.setData( result );
				clientResponse.result = <string> SPARQLResponseType.success;
				clientResponse.query = query;

				return clientResponse;
			},
			( error )=> {
				console.log( error );
			} );
	}

	executeDESCRIBE( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		let beforeTimestamp:number = ( new Date() ).valueOf();
		return this.context.documents.executeRawDESCRIBEQuery( query.endpoint, query.content ).then(
			( [ result, response ]:[ string, HTTP.Response.Class ] ):SPARQLClientResponse => {
				let duration:number = (new Date()).valueOf() - beforeTimestamp;

				let clientResponse:SPARQLClientResponse = new SPARQLClientResponse();
				clientResponse.duration = duration;
				clientResponse.resultset = result;
				clientResponse.setData( result );
				clientResponse.result = <string> SPARQLResponseType.success;
				clientResponse.query = query;

				return clientResponse;
			} );
	}

	executeCONSTRUCT( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		let beforeTimestamp:number = ( new Date() ).valueOf();
		return this.context.documents.executeRawCONSTRUCTQuery( query.endpoint, query.content ).then(
			( [ result, response ]:[ string, HTTP.Response.Class ] ):SPARQLClientResponse => {
				let duration:number = (new Date()).valueOf() - beforeTimestamp;

				let clientResponse:SPARQLClientResponse = new SPARQLClientResponse();
				clientResponse.duration = duration;
				clientResponse.resultset = result;
				clientResponse.setData( result );
				clientResponse.result = <string> SPARQLResponseType.success;
				clientResponse.query = query;

				return clientResponse;
			} );
	}

	executeASK( query:SPARQLQuery ):Promise<SPARQLClientResponse> {
		let beforeTimestamp:number = ( new Date() ).valueOf();
		return this.context.documents.executeRawASKQuery( query.endpoint, query.content ).then(
			( [ result, response ]:[ SPARQL.RawResults.Class, HTTP.Response.Class ] ):SPARQLClientResponse => {
				let duration:number = (new Date()).valueOf() - beforeTimestamp;

				let clientResponse:SPARQLClientResponse = new SPARQLClientResponse();
				clientResponse.duration = duration;
				clientResponse.resultset = result;
				clientResponse.setData( result );
				clientResponse.result = <string> SPARQLResponseType.success;
				clientResponse.query = query;

				return clientResponse;
			} );
	}

	executeUPDATE( query:SPARQLQuery ):Promise {
		return new Promise( ( resolve:() => string, reject:( msg:string ) => string ) => {
			reject( "Unsupported Operation" );
		} );
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

	onClickSavedQuery( selectedQuery:SPARQLQuery ):void {
		if ( JSON.stringify( this.currentQuery ) !== JSON.stringify( selectedQuery ) ) {
			this.askingQuery = <SPARQLQuery>{
				endpoint: selectedQuery.endpoint,
				type: selectedQuery.type,
				content: selectedQuery.content,
				operation: selectedQuery.operation,
				format: selectedQuery.format,
				name: selectedQuery.name,
				id: selectedQuery.id,
			};
			this.toggleConfirmationModal();
		} else {
			this.loadQuery( selectedQuery );
			this.toggleSidebar();
		}
	}

	onClickRemoveSavedQuery( index:number ):void {
		this.savedQueries = this.getLocalSavedQueries();
		this.savedQueries.splice( index, 1 );
		this.updateLocalSavedQueries();
	}

	loadQuery( query:SPARQLQuery ):void {
		this.currentQuery = query;
		this.askingQuery = <SPARQLQuery>{
			endpoint: query.endpoint,
			type: query.type,
			content: query.content,
			operation: query.operation,
			format: query.format,
			name: query.name,
			id: query.id,
		};
		this.endpoint = query.endpoint;
		this.sparql = query.content;
	}

	initializeSavedQueriesSidebar():void {
		this.sidebar.sidebar( {
			context: this.$element.find( ".query-builder .pushable" ),
		} );
	}

	initializeModal():void {
		this.confirmationModal.modal( {
			closable: false,
			blurring: true,
		} );
	}

	toggleConfirmationModal():void {
		this.confirmationModal.modal( "toggle" );
	}

	onApproveConfirmationModal( approvedQuery:SPARQLQuery ):void {
		this.askingQuery = <SPARQLQuery>{};
		this.loadQuery( approvedQuery );
		this.toggleSidebar();
	}

	getLocalSavedQueries():SPARQLQuery[] {
		if ( ! window.localStorage.getItem( "savedQueries" ) )
			this.updateLocalSavedQueries();
		return <SPARQLQuery[]>JSON.parse( window.localStorage.getItem( "savedQueries" ) );
	}

	updateLocalSavedQueries():void {
		window.localStorage.setItem( "savedQueries", JSON.stringify( this.savedQueries ) );
	}

	toggleSidebar():void {
		this.sidebar.sidebar( "toggle" );
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
