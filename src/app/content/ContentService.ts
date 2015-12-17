import { Injectable } from 'angular2/angular2';
import { Http, Response, Request } from 'angular2/http';

import Carbon from 'carbonldp-sdk';

import BlogPost from './../blog/blog-post/BlogPost';

@Injectable()
export default class ContentService {

	static parameters = [ [ Carbon ], [ Http ] ];
	static dependencies = ContentService.parameters;

	carbon:Carbon;
	http:Http;

	data:string;
;
	postsList:BlogPost[];

	constructor( carbon:Carbon, http:Http ) {
		this.carbon = carbon;
		this.http = http;
	}

	getDocumentById( id:string ):Promise<string> {
		return new Promise<string>( ( resolve, reject ) => {
			let url = window.location.href;
			let arr = url.split( "/" );
			let protocolHostAndPort = arr[ 0 ] + "//" + arr[ 2 ];

			this.http.get( protocolHostAndPort + '/assets/documents/' + id )
				.map( res => res.text() )
				.subscribe(
					data => this.data = data,
					err => console.log( err ),
					() => resolve( this.data )
				);
		} );
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
		return new Promise<BlogPost[]>( ( resolve, reject ) => {
			this.http.get( "/app/blog/service/bloglist.json" )
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
