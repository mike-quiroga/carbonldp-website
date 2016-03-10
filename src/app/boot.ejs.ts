/// <reference path="./../../typings/typings.d.ts" />
import "zone.js";
import "reflect-metadata";

import { bootstrap } from "angular2/platform/browser";
import { provide } from "angular2/core";
import { Provider } from "angular2/src/core/di/provider";
import { FORM_PROVIDERS } from "angular2/common";
import { ROUTER_PROVIDERS, APP_BASE_HREF } from "angular2/router";
import { HTTP_PROVIDERS } from "angular2/http";

import Carbon from "carbon/Carbon";

import AppComponent from "app/AppComponent";

import { CONTENT_PROVIDERS } from "app/content/Content";
import { BLOG_PROVIDERS } from "app/website/blog/Blog";
import { APP_DEV_PROVIDERS } from "app/app-dev/AppDev";

const CARBON_PROVIDER:Provider = provide( Carbon, {
	useFactory:():Carbon => {
		let carbon:Carbon = new Carbon();
		carbon.setSetting( "domain", "<%- carbon.domain %>" );
		return carbon;
	},
} );

bootstrap( AppComponent, [
	FORM_PROVIDERS,
	ROUTER_PROVIDERS,
	HTTP_PROVIDERS,

	provide( APP_BASE_HREF, { useValue: "<%- url.base %>" } ),

	CARBON_PROVIDER,
	CONTENT_PROVIDERS,
	BLOG_PROVIDERS,
	APP_DEV_PROVIDERS,
] );
