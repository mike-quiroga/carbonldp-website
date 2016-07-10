import { Component, ElementRef } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { CORE_DIRECTIVES } from "@angular/common";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";
import { Observable } from "rxjs/Rx";

import "semantic-ui/semantic";

import Carbon from "carbonldp/Carbon";
import * as CarbonApp from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";

import { MyAppsSidebarService } from "./../my-apps-sidebar.service";

import AppContextService from "./../../AppContextService";
import { AppTileComponent } from "./app-tile/app-tile.component";
import { AppsListComponent } from "./apps-list/apps-list.component";
import * as App from "./../app/app";

import { Message } from "carbon-panel/errors-area/error-message.component";

import template from "./apps-list.view.html!";
import "./apps-list.view.css!";

@Component( {
	selector: "my-apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppTileComponent, AppsListComponent ],
} )
export class AppsListView {
	apps:App.Class[] = [];
	results:App.Class[] = [];

	loading:boolean = false;
	tileView:boolean = false;
	searchBox:JQuery;
	errorMessage:string = "";
	askingApp:App.Class;

	deleteAppConfirmationModal:JQuery;
	deleting:boolean = false;
	deleteError:Message;

	private element:ElementRef;
	private $element:JQuery;
	private router:Router;
	private carbon:Carbon;
	private appContextService:AppContextService;
	private myAppsSidebarService:MyAppsSidebarService;

	constructor( element:ElementRef, router:Router, appContextService:AppContextService, title:Title, carbon:Carbon, myAppsSidebarService:MyAppsSidebarService ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
		this.appContextService = appContextService;
		this.router = router;
		this.carbon = carbon;
		this.myAppsSidebarService = myAppsSidebarService;

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
			return app.name.toLowerCase().search( term.toLowerCase() ) > - 1 || app.slug.toLowerCase().search( term.toLowerCase() ) > - 1
		} );
		this.errorMessage = "";
		if( this.results.length === 0 && term.length > 0 ) {
			this.errorMessage = "No apps found.";
		}
	}

	askConfirmationToDeleteApp( selectedApp:App.Class ):void {
		this.askingApp = selectedApp;
		this.toggleDeleteConfirmationModal();
	}

	toggleDeleteConfirmationModal():void {
		this.deleteAppConfirmationModal.modal( "toggle" );
		this.deleteError = null;
	}

	onApproveAppDeletion( approvedApp:App.Class ):void {
		if( this.deleting ) return;
		this.deleting = true;
		this.deleteError = null;
		this.deleteApp( approvedApp ).then( ( response:HTTP.Response.Class ):void => {
			this.toggleDeleteConfirmationModal();
			this.apps.splice( this.apps.indexOf( approvedApp ), 1 );
		} ).catch( ( error:HTTP.Errors.Error ):void => {
			this.deleteError = this.getErrorMessage( error );
		} ).then( ():void => {
			this.deleting = false;
			this.searchApp( this.searchBox.val() );
		} );
	}

	openApp( app:App.Class ):void {
		this.myAppsSidebarService.addApp( app );
		this.router.navigate( [ "/AppDev/MyApps/App", { slug: app.slug }, "AppDashboard" ] );
	}

	deleteApp( app:App.Class ):Promise<HTTP.Response.Class> {
		return app.destroy();
	}

	getErrorMessage( error:HTTP.Errors.Error ):Message {
		let content:string = "";
		switch ( true ) {
			case error instanceof HTTP.Errors.ForbiddenError:
				content = "Denied Access.";
				break;
			case error instanceof HTTP.Errors.UnauthorizedError:
				content = "Wrong credentials.";
				break;
			case error instanceof HTTP.Errors.BadGatewayError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.GatewayTimeoutError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.InternalServerErrorError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.UnknownError:
				content = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.ServiceUnavailableError:
				content = "Service currently unavailable.";
				break;
			default:
				content = "There was a problem processing the request. Error: " + error.response.status;
				break;
		}

		return {
			title: error.name,
			content: ! ! error.message ? error.message : content,
			statusCode: "" + error.response.status,
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
		this.loadApps().then( ( apps:App.Class[] ):void => {
			this.results = apps;

			this.loading = false;
		} ).catch( ( error:any ):void => {
			// TODO: Show a more specific error message
			console.error( error );
			this.errorMessage = "An error occurred. Please, try again later.";

			this.loading = false;
		} );
	}

	private loadApps():Promise< App.Class[] > {
		return this.appContextService.getAll().then( ( appContexts:CarbonApp.Context[] ) => {
			this.apps = appContexts.map( App.Factory.createFrom );
			return this.apps;
		} );
	}
}

export default AppsListView;
