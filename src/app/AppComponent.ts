import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig } from "@angular/router-deprecated";

import { Angulartics2GoogleAnalytics } from "angulartics2/src/providers/angulartics2-google-analytics";
import { Angulartics2 } from "angulartics2";

import WebsiteView from "app/website/WebsiteView";
import { AppDevLoginView } from "app/auth/app-dev-login/app-dev-login.view";

import { AppDevView } from "app/app-dev/app-dev.view";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "app",
	template: template,
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

	{ path: "login", as: "AppDevLogin", component: AppDevLoginView },
	// TODO: Remove 'site' portion from the URL. Right now Angular doesn't behave like it should with blank child URLs
	{ path: "site/...", as: "Website", component: WebsiteView },
] )
export default class App {
	// Importing angulartics2, angulartics2GoogleAnalytics as per documentation of angulartics2 plug-in
	constructor( angulartics2:Angulartics2, angulartics2GoogleAnalytics:Angulartics2GoogleAnalytics ) {}
}
