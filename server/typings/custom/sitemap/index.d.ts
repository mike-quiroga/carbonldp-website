declare module "sitemap" {
	export namespace Sitemap {
		export interface URL {
			url:string;
			changefreq?:string;
			priority?:number;
		}

		export interface Options {
			hostname:string;
			cacheTime:number;
			urls:URL[];
		}
	}

	export interface Sitemap {
		toXML( callback:( error:any, xml:string ) => void ):void;
	}

	export interface SitemapBuilder {
		createSitemap( options:Sitemap.Options ):Sitemap;
	}

	let sitemapBuilder:SitemapBuilder;
	export default sitemapBuilder;
}