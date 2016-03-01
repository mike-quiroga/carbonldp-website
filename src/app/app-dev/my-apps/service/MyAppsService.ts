import { Injectable } from 'angular2/core';
import { Http, Response, Request } from 'angular2/http';

import App from './../app/App';


@Injectable()
export default class MyAppsService {
	http:Http;

	data:string;

	postsList:App[];

	constructor( http:Http ) {
		this.http = http;
	}


	getApps():Promise<App[]> {
		return new Promise<App[]>( ( resolve, reject ) => {
			this.http.get( "assets/CarbonApps.json" )
				//.map( res => res.json() )
				.subscribe(
					( res ) => {
						this.postsList = res.json();
						resolve( this.postsList );
					},
					( error ) => {
						reject( error );
					}
				);
		} );
	}

	getapp( slug:string ):Promise<App> {
		return new Promise<App[]>( ( resolve, reject ) => {
			this.getApps().then(
				( apps )=> {
					let app:App = <App>apps.find( app => app.slug == slug );
					resolve( app );
				},
				( error )=> {
					reject( error );
				}
			);
		} );
	}
}