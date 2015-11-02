import { View, Component } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from 'angular2/router';

import Carbon from 'carbonldp-sdk';

import Home from 'app/views/home/component';
import Login from 'app/views/login/component';
import SignUp from 'app/views/signup/component';

import template from './template.html!';

@Component({
	selector: 'app'
})
@View({
	template: template,
	directives: [ ROUTER_DIRECTIVES ]
})
@RouteConfig([
	{ path: '/', redirectTo: '/home' },
	{ path: '/home', as: 'Home', component: Home },
	{ path: '/login', as: 'Login', component: Login },
	{ path: '/signup', as: 'SignUp', component: SignUp }
])
export default class App {
	carbonVersion:string = 'Click to get Version';

	getCarbonVersion():void {
		var carbon = new Carbon();
		carbon.setSetting( 'domain', 'dev.carbonldp.com' );
		carbon.getAPIDescription().then( ( api ) => {
			this.carbonVersion = api.version;
		});
	}
}