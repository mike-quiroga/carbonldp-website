export class MetaTagService {

	getMetaTags( name:string ):HTMLMetaElement[] {
		let metaTags:NodeListOf<HTMLMetaElement> = document.getElementsByTagName( "meta" );
		let metaTagsFound:HTMLMetaElement[] = [];
		for ( let i:number = 0, length:number = metaTags.length; i < length; i ++ ) {
			if( metaTags[ i ].name === name ) metaTagsFound.push( metaTags[ i ] );
		}
		return metaTagsFound;
	}

	setMetaTag( name:string, content:string ):any {
		this.removeMetaTags( name );
		this.addMetaTag( name, content );
	}

	addMetaTag( name:string, content:string ):any {

		let meta:HTMLElement = document.createElement( "meta" );

		meta.setAttribute( "name", name );
		meta.setAttribute( "content", content );
		document.head.appendChild( meta );
	}

	removeMetaTags( name:string ):void {
		let metaTagsToRemove:HTMLMetaElement[] = this.getMetaTags( name );

		for ( let metaTag of metaTagsToRemove ) {
			metaTag.parentNode.removeChild( metaTag );
		}
	}
}