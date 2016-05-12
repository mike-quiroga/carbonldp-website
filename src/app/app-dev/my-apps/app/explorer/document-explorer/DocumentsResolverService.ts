import { Injectable } from "angular2/core";

import Carbon from "carbonldp/Carbon";
import * as HTTP from "carbonldp/HTTP";
import * as NS from "carbonldp/NS";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";

@Injectable()
export default class DocumentsResolverService {

	carbon:Carbon;

	documents:Map<string, RDFDocument.Class>;
	private parser:RDFDocument.Parser = new RDFDocument.Parser();

	constructor( carbon:Carbon ) {
		this.carbon = carbon;
		this.documents = new Map<string, RDFDocument.Class>();
	}

	get( uri:string, documentContext:SDKContext.Class ):Promise<RDFDocument.Class> {
		if ( this.documents.has( uri ) ) return Promise.resolve( this.documents.get( uri ) );
		if ( ! uri || ! documentContext ) return <any> Promise.reject( new Error( "Provide the required parameters" ) );

		let requestOptions:HTTP.Request.Options = {
			sendCredentialsOnCORS: true,
		};

		if ( documentContext && documentContext.auth.isAuthenticated() ) documentContext.auth.addAuthentication( requestOptions );

		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.RDFSource, requestOptions );

		return HTTP.Request.Service.get( uri, requestOptions ).then( ( response:HTTP.Response.Class ) => {
			return this.parser.parse( response.data );
		} ).then( ( parsedDocument:RDFDocument.Class ) => {
			if ( ! parsedDocument[ 0 ] ) return null;

			this.documents.set( uri, parsedDocument );
			return parsedDocument;
		} ).catch( ( error ) => {
			console.error( error );
			return Promise.reject( error );
		} );

		// return new Promise<RDFDocument.Class>(
		// 	( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
		// 		if ( this.documents.has( uri ) ) {
		// 			resolve( this.documents.get( uri ) );
		// 			return;
		// 		}
		// 		if ( ! uri || ! documentContext ) {
		// 			reject( new Error( "Provide the required parameters" ) );
		// 			return;
		// 		}
		// 		let requestOptions:HTTP.Request.Options = {
		// 			sendCredentialsOnCORS: true,
		// 		};
		// 		if ( documentContext && documentContext.auth.isAuthenticated() ) documentContext.auth.addAuthentication( requestOptions );
		// 		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		// 		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.RDFSource, requestOptions );
		// 		let resolveDocument:Promise<HTTP.Response.Class> = HTTP.Request.Service.get( uri, requestOptions );
		// 		resolveDocument.then(
		// 			( response:HTTP.Response.Class ) => {
		// 				this.parser.parse( response.data ).then(
		// 					( parsedDocument:RDFDocument.Class ) => {
		// 						if ( ! parsedDocument[ 0 ] )
		// 							return;
		// 						this.documents.set( uri, parsedDocument );
		// 						resolve( parsedDocument );
		// 					},
		// 					( error:HTTP.Errors.Error ) => {
		// 						return error;
		// 					}
		// 				);
		// 			}
		// 		).catch(
		// 			( error ) => {
		// 				console.log( error );
		// 				reject( error );
		// 			}
		// 		);
		// 	}
		// );
	}

	getAll():Promise<RDFDocument.Class[]> {
		return new Promise<RDFDocument.Class[]>( ( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
			let keys = Object.keys( this.documents );
			let values = keys.map( ( v ) => { return this.documents[ v ]; } );
			resolve( values );
		} );
	}
}
