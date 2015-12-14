import { Injectable } from 'angular2/angular2';
import { Http, Response, Request } from 'angular2/http';

import Carbon from 'carbonldp-sdk';

import BlogPost from './../blog-post/BlogPost';

@Injectable()
export default class BlogService {
	static parameters = [ [ Carbon ], [ Http ] ];
	static dependencies = BlogService.parameters;

	carbon:Carbon;
	http:Http;
	postsList:BlogPost[];

	constructor( carbon:Carbon, http:Http ) {
		this.carbon = carbon;
		this.http = http;
	}

	getPost( filename:string ):Promise<string> {
		return new Promise<string>( ( resolve, reject ) => {
			//this.http.get( "app/blog-test/posts/" + filename )
			this.http.get( "assets/blog-posts/" + filename )
				.map( res => res.text() )
				.subscribe(
					( res ) => {
						resolve( res );
					},
					( error ) => {
						console.error( error );
					}
				);
		} ).catch( console.error );
	}

	getPostsList():Promise<BlogPost[]> {
		return new Promise<BlogPost[]>( ( resolve, reject ) => {
			this.http.get( "app/blog-test/service/bloglist.json" )
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