/// <reference path="./../../../../typings/typings.d.ts" />
import { Injectable } from 'angular2/core';
import { Http, Response, Request } from 'angular2/http';
import { Location } from "angular2/router";

import BlogPost from './../blog-post/BlogPost';

@Injectable()
export default class BlogService {

	static parameters = [ [ Http ], [ Location ] ];
	static dependencies = BlogService.parameters;

	http:Http;
	location:Location;

	data:string;

	postsList:BlogPost[];

	constructor( http:Http, location:Location ) {
		this.http = http;
		this.location = location;
	}

	getPost( id:number ):Promise<BlogPost> {

		return new Promise<BlogPost[]>( ( resolve, reject ) => {
			this.getPostsList().then(
				( posts )=> {
					let post:BlogPost = posts[ id ];
					post.creationDate = new Date( Date.parse( post.creationDate.toString() ) );
					resolve( post );
				},
				( error )=> {
					console.log( error );
				}
			).catch( console.error );
		} ).catch( console.error );
	}

	getPostsList():Promise<BlogPost[]> {
		return new Promise<BlogPost[]>(
			( resolve ) => {
				this.http.get( `${ this.location.platformStrategy.getBaseHref() }assets/blog-posts/bloglist.json` )
					.forEach(
						( response ) => {
							resolve( response.json() );
						}, this
					);
			}
		).catch( console.error );
	}

}
