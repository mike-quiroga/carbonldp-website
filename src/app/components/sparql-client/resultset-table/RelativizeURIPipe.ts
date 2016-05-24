import { Pipe, PipeTransform } from "@angular/core";

import * as URI from "carbonldp/RDF/URI";

@Pipe( { name: "relative" } )
export default class RelativizeURIPipe implements PipeTransform {
	transform( value:string, args:any[] ):string {
		if ( args.length === 0 ) throw new Error( "The relative pipe requires an argument" );
		let baseURI:string = args[ 0 ];

		if ( ! value.startsWith( baseURI ) ) return value;

		return URI.Util.getRelativeURI( value, baseURI );
	}
}
