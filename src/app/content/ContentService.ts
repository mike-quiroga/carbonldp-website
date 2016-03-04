/// <reference path="./../../../typings/typings.d.ts" />
import { Injectable, OpaqueToken } from "angular2/core";
import { Http, Response, Request } from "angular2/http";
import { Location } from "angular2/router";

import Carbon from "carbon/Carbon";


@Injectable()
export default class ContentService {

	static parameters = [ [ Carbon ], [ Http ], [ Location ] ];
	static dependencies = ContentService.parameters;

	carbon:Carbon;
	http:Http;

	location:Location;

	data:string;

	constructor( carbon:Carbon, http:Http, location:Location ) {
		this.carbon = carbon;
		this.http = http;
		this.location = location;
	}

	getDocumentById( id:string ):Promise<string> {
		return new Promise<string>( ( resolve, reject ) => {
			this.http.get( `${ this.location.platformStrategy.getBaseHref() }assets/documents/${ id }` )
				.forEach(
					( response ) => {
						this.data = response.text();
						resolve( this.data );
					}, this
				);
		} );
	}

}
