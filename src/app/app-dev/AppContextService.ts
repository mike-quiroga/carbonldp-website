import { Injectable } from 'angular2/core';

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

@Injectable()
export default class AppContextService {

	carbon:Carbon;

	appContexts:Map<string, App.Context>;

	constructor( carbon:Carbon ) {
		this.carbon = carbon;
		this.appContexts = new Map<string, App.Context>();
	}

	get( slug:string ):Promise<App.Context> {
		return new Promise<App.Context>( ( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
			if ( this.appContexts.has( slug ) ) {
				resolve( this.appContexts.get( slug ) );
				return;
			}

			this.carbon.apps.get( slug + "/" ).then(
				( appContext:App.Context ) => {
					this.appContexts.set( slug, appContext );
					resolve( appContext );
				}
			).catch(
				( error )=> {
					console.log( error );
					reject( error );
				}
			);
		} );
	}

	getAll():Promise<App.Context[]> {
		return this.carbon.apps.getAll().then( ( appContexts:App.Context[] ) => {
			appContexts
				.filter( ( appContext:App.Context ) => {
					return ! this.appContexts.has( this.getSlug( appContext ) );
				} )
				.forEach( ( appContext:App.Context ) => {
					this.appContexts.set( this.getSlug( appContext ), appContext );
				} );

			return Utils.A.from( this.appContexts.values() );
		} );
	}

	getSlug( appContext:App.Context ):string {
		let uri:string = appContext.app.id;

		return this.removeTrailingSlash( URI.Util.getSlug( uri ) );
	}

	private removeTrailingSlash( slug:string ):string {
		if ( slug.endsWith( "/" ) ) {
			return slug.substr( 0, slug.length - 1 );
		} else {
			return slug;
		}
	}
}
