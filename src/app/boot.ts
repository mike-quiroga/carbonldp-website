import 'zone.js';
import 'reflect-metadata';

import { bootstrap, provide, FORM_PROVIDERS } from 'angular2/angular2';
import { ROUTER_PROVIDERS, APP_BASE_HREF } from 'angular2/router';
import { HTTP_PROVIDERS } from 'angular2/http';

import App from 'app/component';

bootstrap( App, [
	FORM_PROVIDERS,
	ROUTER_PROVIDERS,
	HTTP_PROVIDERS,
	provide( APP_BASE_HREF, { useValue: '/' } )
]);