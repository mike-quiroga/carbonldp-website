import { Component } from "angular2/core";
import { Title } from "angular2/platform/browser" ;
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";
import { Component, ElementRef, Type } from "angular2/core";
import { CORE_DIRECTIVES, Control } from "angular2/common";
import { Router, RouteDefinition, ROUTER_DIRECTIVES, CanActivate} from "angular2/router";
import { Observable } from "rxjs/Rx";
import "rxjs/add/operator/map";
import "rxjs/add/operator/debounceTime";
import "rxjs/add/operator/distinctUntilChanged";
import "rxjs/add/operator/switchMap";

import "semantic-ui/semantic";

import AppContextService from "./../../AppContextService";

import AppTileComponent from "./../app-tile/AppTileComponent";
import AppsListComponent from "../apps-list/AppsListComponent";
import App from "./../app/App";

import template from "./template.html!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent, AppsListComponent ],
} )
export default class AppsListView {
	router:Router;
	element:ElementRef;
	$element:JQuery;

	appContextService:AppContextService;
	apps:App[] = [];
	results:App[] = [];

	loading:boolean = false;
	tileView:boolean = false;
	searchBox:JQuery;
	errorMessage:string = "";

	constructor( element:ElementRef, router:Router, appContextService:AppContextService, title:Title ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
		this.appContextService = appContextService;
		this.router = router;
		title.setTitle( "My Apps" );
	}

	ngAfterViewInit():void {
		this.searchBox = this.$element.find( "input.search" );
		let terms:any = Observable.fromEvent( this.searchBox, "input" );
		terms
			.debounceTime( 200 )
			.map( ( evt ) => {
				return evt.target.value;
			} )
			.distinctUntilChanged()
			.subscribe(
				( args ):void => {
					this.searchApp( args );
				}
			);
	}

	activateGridView():void {
		this.tileView = true;
	}

	activateListView():void {
		this.tileView = false;
	}

	searchApp( term:string ):void {
		this.results = this.apps.filter( ctx => ctx.app.name.toLowerCase().search( term.toLowerCase() ) > - 1 || ctx.slug.toLowerCase().search( term.toLowerCase() ) > - 1 );
		this.errorMessage = "";
		if ( this.results.length === 0 && term.length > 0 ) {
			this.errorMessage = "No apps found.";
		}
	}

	routerOnActivate():void {
		this.loading = true;
		this.appContextService.getAll().then(
			( appContexts:any ):void => {
				appContexts.forEach( ( appContext ) => {
					this.apps.push( <App>{
						slug: this.appContextService.getSlug( appContext ),
						app: appContext.app,
					} );
				} );
				this.results = this.apps;
			},
			( error:any ):void => {
				console.error( error );
				this.errorMessage = "An error occurred. Please, try again later.";
			}
		).then(
			():void => {
				this.loading = false;
			}
		);
	}
}
