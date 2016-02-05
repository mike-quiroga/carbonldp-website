import { Injectable } from 'angular2/core';
import { Http, Response, Request } from 'angular2/http';

import CarbonApp from './../carbon-app/CarbonApp';
import {error} from "util";

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
			this.http.get( "/assets/CarbonApps.json" )
				//.map( res => res.json() )
				.subscribe(
					( res ) => {
						this.postsList = res.json();
					},
					( error ) => {
						console.error( error );
					},
					() => {
						resolve( this.postsList );
					}
				);
		} ).catch( console.error );
	}

}