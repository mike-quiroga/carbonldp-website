import Express from "express";
import compression from "compression";
import yargs from "yargs";
import opn from "opn";

const argv:AppBooterOptions = <any>yargs
	.usage( "Usage: -r rootDirectory [-b baseURL]" )

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

			this.app.get( "*", ( request:Express.Request, response:Express.Response ):void => {
				// TODO: Make file location configurable
				response.render( "index.ejs", {
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
					title: title,
					description: description
				} );
			} );
		}
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
