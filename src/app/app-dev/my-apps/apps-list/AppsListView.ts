import { Component, ElementRef } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { CORE_DIRECTIVES } from "@angular/common";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";
import { Observable } from "rxjs/Rx";

import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as CarbonApp from "carbonldp/App";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as HTTPErrors from "carbonldp/HTTP/Errors";
import * as HTTPError from "carbonldp/HTTP/Errors/HTTPError";

import AppContextService from "./../../AppContextService";
import AppTileComponent from "./app-tile/AppTileComponent";
import AppsListComponent from "./apps-list/AppsListComponent";
import App from "./../app/App";
import Message from "./../../components/errors-area/ErrorsAreaComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent, AppsListComponent ],
} )
export default class AppsListView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	carbon:Carbon;

	appContextService:AppContextService;
	apps:App[] = [];
	results:App[] = [];

	loading:boolean = false;
	tileView:boolean = false;
	searchBox:JQuery;
	errorMessage:string = "";
	askingApp:App;

	deleteAppConfirmationModal:JQuery;
	deleting:boolean = false;
	deleteError:Message;

	constructor( element:ElementRef, router:Router, appContextService:AppContextService, title:Title, carbon:Carbon ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
		this.appContextService = appContextService;
		this.router = router;
		this.carbon = carbon;
		title.setTitle( "My Apps" );
	}

	ngOnInit():void {
		this.deleteAppConfirmationModal = this.$element.find( ".delete-app-confirmation.modal" );
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
		this.initializeModal();
	}

	activateGridView():void {
		this.tileView = true;
	}

	activateListView():void {
		this.tileView = false;
	}

	searchApp( term:string ):void {
		this.results = this.apps.filter( ( app ) => {
			return app.appContext.name.toLowerCase().search( term.toLowerCase() ) > - 1 || app.slug.toLowerCase().search( term.toLowerCase() ) > - 1
		} );
		this.errorMessage = "";
		if ( this.results.length === 0 && term.length > 0 ) {
			this.errorMessage = "No apps found.";
		}
	}

	askConfirmationToDeleteApp( selectedApp:App ):void {
		this.askingApp = selectedApp;
		this.toggleDeleteConfirmationModal();
	}

	toggleDeleteConfirmationModal():void {
		this.deleteAppConfirmationModal.modal( "toggle" );
		this.deleteError = null;
	}

	onApproveAppDeletion( approvedApp:App ):void {
		if ( this.deleting ) {
			return;
		}
		this.deleting = true;
		this.deleteError = null;
		this.deleteApp( approvedApp ).then(
			( response:HTTP.Response.Class ):void => {
				this.toggleDeleteConfirmationModal();
				this.apps.splice( this.apps.indexOf( approvedApp ), 1 );
			},
			( error:HTTPError.HTTPError ):void => {
				this.deleteError = this.getErrorMessage( error );
			}
		).then(
			():void => {
				this.deleting = false;
				this.searchApp( this.searchBox.val() );
			}
		);
	}

	deleteApp( app:App ):Promise<HTTP.Response.Class> {
		return (<PersistedDocument.Class>app.appContext).destroy();
	}

	getErrorMessage( error:HTTPError.HTTPError ):Message {
		let content:string = "";
		switch ( true ) {
			case error instanceof HTTPErrors.ForbiddenError:
				content = "Denied Access.";
			case error instanceof HTTPErrors.UnauthorizedError:
				content = "Wrong credentials.";
			case error instanceof HTTPErrors.BadGatewayError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
			case error instanceof HTTPErrors.GatewayTimeoutError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
			case error instanceof HTTPErrors.InternalServerErrorError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
			case error instanceof HTTPErrors.UnknownError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
			case error instanceof HTTPErrors.ServiceUnavailableError:
				content = "Service currently unavailable.";
			default:
				content = "There was a problem processing the request. Error: " + error.response.status;
		}
		return <Message>{
			title: error.name,
			content: ! ! error.message ? error.message : content,
			statusCode: error.response.status,
			statusMessage: error.response.request.statusText,
			endpoint: "",
		};
	}

	closeErrorMessage( evt:any ):void {
		$( evt.srcElement ).closest( ".ui.message" ).transition( "fade" );
		this.deleteError = null;
	}

	initializeModal():void {
		this.deleteAppConfirmationModal.modal( {
			closable: false,
			blurring: true,
			onApprove: ():boolean => { return false; },
		} );
	}

	routerOnActivate():void {
		this.loading = true;
		this.appContextService.getAll().then(
			( appContexts:any ):void => {
				appContexts.forEach( ( appContext ) => {
					this.apps.push( <App>{
						slug: this.appContextService.getSlug( appContext ),
						appContext: appContext.app,
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
