import { Injectable } from 'angular2/angular2';
import { Http, Response, Request } from 'angular2/http';

import BlogPost from './../blog-post/BlogPost';

@Injectable()
export default class CarbonAppsService {

	static parameters = [ [ Http ] ];
	static dependencies = CarbonAppsService.parameters;

	http:Http;

	data:string;

	postsList:BlogPost[];

	constructor( http:Http ) {
		this.http = http;
	}



	getAppsList():Promise<BlogPost[]> {
		return new Promise<BlogPost[]>( ( resolve, reject ) => {
			this.http.get( "/app/app-dev/my-apps/service/CarbonApp.json" )
				.map( res => res.json() )
				.subscribe(
					( res ) => {
						this.postsList = res;
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