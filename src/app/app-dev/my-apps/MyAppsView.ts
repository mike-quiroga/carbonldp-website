import {Component} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {ROUTER_DIRECTIVES, RouteConfig, RouterOutlet} from "angular2/router";


import AppDetailView from "./app/AppDetailView";
import AppsListView from "./apps-list/AppsListView";

@Component( {
	selector: "my-apps",
	template: "<router-outlet></router-outlet>",
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, RouterOutlet ],
} )
@RouteConfig( [
	{
		path: "/",
		as: "List",
		component: AppsListView,
		useAsDefault: true,
		data: {
			alias: "List",
			displayName: "My Apps",
		},
	},
	{
		path: "/:slug/...",
		as: "App",
		component: AppDetailView,
		data: {
			alias: "App",
			displayName: "App",
			params: {
				name: "slug",
				redirectTo: "AppDashboard",
			},
		},
	},
] )
export default class MyAppsView { }
