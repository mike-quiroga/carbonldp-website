import { Observable } from "rxjs/Rx";
import "rxjs/operator/toPromise";

import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { Location } from "@angular/common";

import BlogPost from "./../blog-post/BlogPost";

@Injectable()
export default class BlogService {
	http: Http;
	location: Location;

	data: string;

	constructor( http: Http, location: Location ) {
		this.http = http;
		this.location = location;
	}

	getPost( id: number ): Promise<BlogPost> {
		return this.getPostsList().then( ( posts )=> {
			let post: BlogPost = posts[ id ];
			post.creationDate = new Date( Date.parse( post.creationDate.toString() ) );
			return post;
		} );
	}

	getPostsList(): Promise<BlogPost[]> {
		return this.http.get( `${ this.location.platformStrategy.getBaseHref() }assets/blog-posts/bloglist.json` )
			.toPromise()
			.then( ( response ) => {
				return response.json();
			} );
	}

}
