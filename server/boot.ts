import Express from "express";
import compression from "compression";

import { Sitemap } from "sitemap";
import SitemapBuilder from "sitemap";

import yargs from "yargs";
import opn from "opn";


const argv:AppBooterOptions = yargs
	.usage( "Usage: -r rootDirectory [-b baseURL]" )

	.describe( "hostname", "Hostname the website is going to be accessible through" )
	.default( "hostname", "http://localhost" )

	.describe( "root", "Active profile to load configuration from" )
	.demand( "root", "You need to specify a root directory for the server to serve files from" )

	.describe( "port", "Port for the server to listen on" )
	.number( "port" )
	.default( "port", generateRandomNumber( 3000, 4000 ) )

	.describe( "route-table", "JSON file to pull routes metadata from" )
	.demand( "route-table", "You need to specify the JSON file where the route metadata is stored" )

	.describe( "base", "Base URL the website is going to load resource from" )
	.default( "base", "/" )

	.describe( "open-browser", "Open browser window automatically after server starts" )
	.default( "open-browser", false )

	.argv;

function generateRandomNumber( min:number, max:number ):number {
	min = Math.ceil( min );
	max = Math.floor( max );
	return Math.floor( Math.random() * (max - min + 1) ) + min;
}

export interface AppBooterOptions {
	root:string;

	hostname:string;
	port:number;
	base:string;
	routeTable:string;
	openBrowser:boolean;
}

export interface Route {
	data?:{
		title:string;
		description?:string;
	},
	children?:RouteMap
}

type RouteMap = { [ name:string ]:Route };

export class AppBooter {
	private app:Express.Application;

	constructor( private options:AppBooterOptions ) {
		this.options = this.validateOptions( options );
	}

	init():void {
		console.log( process.env.NODE_ENV );

		this.getRouteTable().then( ( routeMap:RouteMap ) => {
			this.app = Express();

			this.app.set( "views", this.options.root + this.options.base );
			this.app.set( "view engine", "ejs" );

			this.app.use( compression() );
			this.app.use( Express.static( this.options.root ) );

			this.registerDynamicRoutes( routeMap );

			let sitemap:Sitemap = this.createSitemap( routeMap );

			this.app.get( "/sitemap.xml", ( request:Express.Request, response:Express.Response ):void => {
				sitemap.toXML( ( error:any, xml:string ):void => {
					if( error ) {
						console.error( error );
						return response.status( 500 ).end();
					}
					response.header( "Content-Type", "application/xml" );
					response.send( xml );
				} );
			} );

			this.app.get( "*", ( request:Express.Request, response:Express.Response ):void => {
				// TODO: Make file location configurable
				response.render( "index.ejs", {
					base: this.options.base,
					title: "CarbonLDP",
					description: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web"
				} );
			} );

			this.app.listen( this.options.port, () => {
				console.log( `Server's up! Listening on port ${ this.options.port }` );
			} );
		} ).then( () => {
			if( this.options.openBrowser ) this.openBrowser();
		} ).catch( console.error );
	}

	getRouteTable():Promise<RouteMap> {
		return SystemJS.import( `${ this.options.root }${ this.options.base }${ this.options.routeTable }!` );
	}

	validateOptions( options:AppBooterOptions ):AppBooterOptions {
		options.root = options.root.endsWith( "/" ) ? options.root.substring( 0, options.root.length - 1 ) : options.root;

		options.base = options.base.startsWith( "/" ) ? options.base : "/" + options.base;
		options.base = options.base.endsWith( "/" ) ? options.base : options.base + "/";

		return options;
	}

	registerDynamicRoutes( routes:RouteMap, base:string = this.options.base ):void {
		for ( let routeName in routes ) {
			if( ! routes.hasOwnProperty( routeName ) ) continue;

			let route:Route = routes[ routeName ];
			let routePath:string = base + routeName;

			routePath = routePath ? routePath : "/";

			// TODO: Configure defaults on another place
			let title:string = route.data ? route.data.title : "Carbon LDP";
			let description:string = route.data && route.data.description ? route.data.description : "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web";

			if( route.children ) this.registerDynamicRoutes( route.children, routePath.endsWith( "/" ) ? routePath : routePath + "/" );

			this.app.get( routePath, ( request:Express.Request, response:Express.Response ):void => {
				// TODO: Make file location configurable
				response.render( "index.ejs", {
					base: this.options.base,
					title: title,
					description: description
				} );
			} );
		}
	}

	createSitemap( routes:RouteMap ):Sitemap {
		let urls:Sitemap.URL[] = this.generateSitemapURLs( routes );

		let hostname:string = this.options.port === <any>"80" || this.options.port === 80 ? this.options.hostname : `${ this.options.hostname }:${ this.options.port }`;

		return SitemapBuilder.createSitemap( {
			hostname: hostname,
			cacheTime: 600 * 1000,
			urls: urls
		} );
	}

	generateSitemapURLs( routes:RouteMap, base:string = this.options.base ):Sitemap.URL[] {
		let urls:Sitemap.URL[] = [];

		for ( let routeName in routes ) {
			if( ! routes.hasOwnProperty( routeName ) ) continue;

			let route:Route = routes[ routeName ];
			let routePath:string = base + routeName;

			routePath = routePath ? routePath : "/";

			urls.push( {
				url: routePath
			} );

			if( route.children ) urls = urls.concat( this.generateSitemapURLs( route.children, routePath.endsWith( "/" ) ? routePath : routePath + "/" ) );
		}

		return urls;
	}

	openBrowser():Promise<any> {
		return new Promise( ( resolve:() => void, reject:( error:any ) => void ) => {
			opn( `http://localhost:${ this.options.port }${ this.options.base }`, { wait: false }, ( error ) => {
				if( error ) reject( error );
				else resolve();
			} );
		} );
	}
}

(new AppBooter( argv )).init();
