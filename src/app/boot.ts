import 'zone.js';
import 'reflect-metadata';

import { bootstrap, provide, FORM_PROVIDERS } from 'angular2/angular2';
import { ROUTER_PROVIDERS, APP_BASE_HREF } from 'angular2/router';
import { HTTP_PROVIDERS } from 'angular2/http';

import Carbon from 'carbonldp-sdk';

import AppComponent from 'app/AppComponent';

import { BLOG_PROVIDERS } from 'app/blog/Blog';

const CARBON_PROVIDER = provide( Carbon, {
	useFactory: () => {
		var carbon = new Carbon();
		carbon.setSetting( 'domain', 'dev.carbonldp.com' );
	}
});

bootstrap( AppComponent, [
	FORM_PROVIDERS,
	ROUTER_PROVIDERS,
	HTTP_PROVIDERS,
	provide( APP_BASE_HREF, { useValue: '/' } ),

	CARBON_PROVIDER,

	BLOG_PROVIDERS,
]);