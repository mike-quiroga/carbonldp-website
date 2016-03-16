import { bootstrap } from "angular2/platform/browser";
import { provide, Injector, ComponentRef } from "angular2/core";
import { FORM_PROVIDERS } from "angular2/common";
import { ROUTER_PROVIDERS, APP_BASE_HREF } from "angular2/router";
import { HTTP_PROVIDERS } from "angular2/http";

import Carbon from "carbon/Carbon";

import AppComponent from "app/AppComponent";

import { BLOG_PROVIDERS } from "app/website/blog/Blog";
import { APP_DEV_PROVIDERS } from "app/app-dev/AppDev";


let appInjectorRef:Injector;
export const appInjector = ( injector?:Injector ):Injector => {
	if ( injector ) {
		appInjectorRef = injector;
	}
	return appInjectorRef;
};

const CARBON_PROVIDER = provide( Carbon, {
	useFactory: () => {
		let carbon = new Carbon();
		carbon.setSetting( "domain", "<%- carbon.domain %>" );
		return carbon;
	}
} );

bootstrap( AppComponent, [
	FORM_PROVIDERS,
	ROUTER_PROVIDERS,
	HTTP_PROVIDERS,

	provide( APP_BASE_HREF, { useValue: "<%- url.base %>" } ),

	CARBON_PROVIDER,
	BLOG_PROVIDERS,
	APP_DEV_PROVIDERS
] ).then(
	( appRef:ComponentRef ) => {
		appInjector( appRef.injector );
	}
);