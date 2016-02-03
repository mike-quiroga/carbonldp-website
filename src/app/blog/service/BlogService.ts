import { Injectable } from 'angular2/core';
import { Http, Response, Request } from 'angular2/http';

import BlogPost from './../blog-post/BlogPost';

@Injectable()
export default class BlogService {

	static parameters = [ [ Http ] ];
	static dependencies = BlogService.parameters;

	http:Http;

	data:string;

	postsList:BlogPost[];

	constructor( http:Http ) {
		this.http = http;
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
				this.http.get( "/assets/blog-posts/bloglist.json" )
					.forEach(
						( response ) => {
							resolve( response.json() );
						}, this
					);
			}
		).catch( console.error );
	}

}
