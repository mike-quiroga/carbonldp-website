/// <reference path="./../../typings/typings.d.ts" />
import "zone.js";
import "reflect-metadata";

import { bootstrap } from "angular2/platform/browser";
import { provide } from "angular2/core";
import { FORM_PROVIDERS } from "angular2/common";
import { ROUTER_PROVIDERS, APP_BASE_HREF } from "angular2/router";
import { HTTP_PROVIDERS } from "angular2/http";

import Carbon from "carbon/Carbon";

import AppComponent from "app/AppComponent";

import { CONTENT_PROVIDERS } from "app/content/Content";
import { BLOG_PROVIDERS } from "app/blog/Blog";

const CARBON_PROVIDER = provide( Carbon, {
	useFactory: () => {
		var carbon = new Carbon();
		carbon.setSetting( "domain", "<%- carbon.domain %>" );
	}
} );

bootstrap( AppComponent, [
	FORM_PROVIDERS,
	ROUTER_PROVIDERS,
	HTTP_PROVIDERS,

	provide( APP_BASE_HREF, { useValue: "<%- url.base %>" } ),

	CARBON_PROVIDER,
	CONTENT_PROVIDERS,
	BLOG_PROVIDERS
] );