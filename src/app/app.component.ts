import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, Router } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";
import { Meta } from "./website/meta";

import { Angulartics2GoogleAnalytics } from "angulartics2/src/providers/angulartics2-google-analytics";
import { Angulartics2 } from "angulartics2";

import WebsiteView from "./website/website.view";
import { AppDevLoginView } from "app/auth/app-dev-login/app-dev-login.view";
import { AppDevView } from "app/app-dev/app-dev.view";

import { NotFoundErrorView } from "app/error-pages/not-found-error/not-found-error.view";

import template from "./app.component.html!";
import style from "./app.component.css!text";

@Component( {
	selector: "app",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
	providers: [ Angulartics2GoogleAnalytics ],
} )
@RouteConfig( [
	{
		path: "app-dev/...", as: "AppDev", component: AppDevView,
		data: {
			alias: "AppDev",
			displayName: "Home",
		},
	},
	{ path: "", redirectTo: [ "./Website" ] },
	{
		path: "login", as: "AppDevLogin", component: AppDevLoginView,
		data: {
			alias: "AppDevLogIn",
			displayName: "Carbon LDP Log In",
		},
	},
	// TODO: Remove 'site' portion from the URL. Right now Angular doesn't behave like it should with blank child URLs
	{
		path: "site/...", as: "Website", component: WebsiteView,
		data: {
			alias: "Carbon LDP",
			displayName: "Carbon LDP",
		},
	},
	{
		path: "**", as: "NotFoundError", component: NotFoundErrorView,
		data: {
			alias: "NotFound",
			displayName: "404 Page Not Found",
		},
	},
] )
export class AppComponent {
	router:Router;
	title:Title;
	meta:Meta;
	// Importing angulartics2, angulartics2GoogleAnalytics as per documentation of angulartics2 plug-in
	constructor( meta:Meta, title:Title, router:Router, angulartics2:Angulartics2, angulartics2GoogleAnalytics:Angulartics2GoogleAnalytics ) {
		this.router = router;
		this.title = title;
		this.meta = meta;
		this.router.subscribe( () => {
			this.defineTitle();
		} );
	}

	defineTitle() {
		let title:string = "";
		let rootComponent = this.router.root.currentInstruction.component.routeData.data[ "displayName" ];
		let auxRouter = this.router.root.currentInstruction.child;

		if( rootComponent === "Home" )
			rootComponent = "Carbon LDP";

		while ( auxRouter !== null ) {
			let displayName = auxRouter.component.routeData.data[ "displayName" ];
			let mainComponent = auxRouter.component.routeData.data[ "main" ];
			let parameters = auxRouter.component.params;

			let parameter = null;
			for ( let parameterName in parameters ) {
				if( ! parameters.hasOwnProperty( parameterName ) ) continue;
				if( parameter !== null ) {
					parameter = null;
					break;
				}
				parameter = parameters[ parameterName ];
			}
			if( parameter !== null ) {
				if( auxRouter.child === null ) {
					if( typeof displayName === 'undefined' ) title = "";
					else title += displayName + "(" + parameter + ") | ";
				} else {
					if( mainComponent )
						title += displayName + "(" + parameter + ") > ";
				}

			} else {
				if( auxRouter.child === null ) {
					if( typeof displayName === 'undefined' ) title = "";
					else title += displayName + " | ";
				} else {
					if( mainComponent ) title = title + displayName + " > ";
				}

			}
			auxRouter = auxRouter.child;
		}

		title += rootComponent;
		if( title === "Home | Carbon LDP" )
			title = "Dashboard | Carbon LDP";
		this.title.setTitle( title );
		console.log( this.meta.setMeta() );
	}

}

export default AppComponent;
