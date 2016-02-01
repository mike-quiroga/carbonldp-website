import { Component, ElementRef } from 'angular2/angular2';
import { Router, RouteConfig, RouterOutlet } from 'angular2/router';

import MyAppsView from './my-apps-view/MyAppsView';

import template from './template.html!';

@Component( {
	selector: 'my-apps-component',
	template: template,
	directives: [ RouterOutlet ]
} )
@RouteConfig( [
	{
		path: '/',
		component: MyAppsView,
		as: 'Home',
		data: {
			alias: "Home",
			displayName: "My Apps"
		}
	}
] )
export default class MyAppsComponent {
	static parameters = [ [ Router ] ];

	router:Router;

	constructor( router:Router ) {
		this.router = router;
	}
}