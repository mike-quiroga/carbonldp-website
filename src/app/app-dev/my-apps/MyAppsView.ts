import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, RouterOutlet } from "@angular/router-deprecated";


import AppDetailView from "./app/AppDetailView";
import AppsListView from "./apps-list/AppsListView";
import CreateAppView from "./create-app/CreateAppView";

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
	{
		path: "/create",
		as: "Create",
		component: CreateAppView,
		data: {
			alias: "Create",
			displayName: "Create App",
		},
	},
] )
export default class MyAppsView { }
