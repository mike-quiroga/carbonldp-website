import { Injectable } from 'angular2/core';
import { Http, Response, Request } from 'angular2/http';

import CarbonApp from './../carbon-app/CarbonApp';


@Injectable()
export default class MyAppsService {

	static parameters = [ [ Http ] ];
	static dependencies = MyAppsService.parameters;

	http:Http;

	data:string;

	postsList:CarbonApp[];

	constructor( http:Http ) {
		this.http = http;
	}


	getApps():Promise<CarbonApp[]> {
		return new Promise<CarbonApp[]>( ( resolve, reject ) => {
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

	getapp( slug:string ):Promise<CarbonApp> {
		return new Promise<CarbonApp[]>( ( resolve, reject ) => {
			this.getApps().then(
				( apps )=> {
					let carbonApp:CarbonApp = <CarbonApp>apps.find( app => app.slug == slug );
					resolve( carbonApp );
				},
				( error )=> {
					reject( error );
				}
			);
		} );
	}
}