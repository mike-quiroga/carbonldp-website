import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, Router } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";
import { MetaTagService } from "./website/meta-tag.service";

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
			description: {
				name: "description",
				content: "Application Developer's Console, a GUI that helps you visualize and manage applications and data outside of code. Visualize your container hierarchy and click resources to inspect data.",
			}
		},

	},
	{ path: "", redirectTo: [ "./Website" ] },
	{
		path: "login", as: "AppDevLogin", component: AppDevLoginView,
		data: {
			alias: "AppDevLogIn",
			displayName: "Carbon LDP Log In",
			description: {
				name: "description",
				content: "Application Developer's Console, a GUI that helps you visualize and manage applications and data outside of code. Visualize your container hierarchy and click resources to inspect data.",
			}
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
	metaTagService:MetaTagService;
	// Importing angulartics2, angulartics2GoogleAnalytics as per documentation of angulartics2 plug-in
	constructor( metaTagService:MetaTagService, title:Title, router:Router, angulartics2:Angulartics2, angulartics2GoogleAnalytics:Angulartics2GoogleAnalytics ) {
		this.router = router;
		this.title = title;
		this.metaTagService = metaTagService;
		this.router.subscribe( () => {
			this.defineTags();
		} );
	}

	defineTags() {
		let title:string = "";
		let metaTag = "";
		let rootComponent = this.router.root.currentInstruction.component.routeData;
		let auxRouter = this.router.root.currentInstruction.child;

		while ( auxRouter !== null ) {
			let displayName = auxRouter.component.routeData.data[ "displayName" ];
			let mainComponent = auxRouter.component.routeData.data[ "main" ];
			metaTag = auxRouter.component.routeData.data[ "description" ];
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

		if( rootComponent.data[ "displayName" ] === "Home" )
			rootComponent.data[ "displayName" ] = "Carbon LDP";

		title += rootComponent.data[ "displayName" ];

		if( title === "Home | Carbon LDP" ) title = "Dashboard | Carbon LDP";

		this.title.setTitle( title );
		this.metaTagService.setMetaTag( "title", title );
		this.metaTagService.setMetaTag( "og:title", title );

		if( typeof metaTag === 'undefined' || ! metaTag ) {
			this.metaTagService.removeMetaTags( "description" );
			this.metaTagService.removeMetaTags( "og:description" );
		}
		else {
			let ogMetaTag = "og:" + metaTag[ "name" ];
			this.metaTagService.setMetaTag( metaTag[ "name" ], metaTag[ "content" ] );
			this.metaTagService.setMetaTag( ogMetaTag, metaTag[ "content" ] );
		}
	}


}

export default AppComponent;
