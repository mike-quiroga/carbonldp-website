import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { Router } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import MyAppsService from './../service/MyAppsService';
import CarbonAppThumbnailComponent from './../carbon-app-thumbnail/CarbonAppThumbnailComponent';
import CarbonApp from "./../carbon-app/CarbonApp";
import CarbonAppView from "./../carbon-app/CarbonAppView";

import template from './template.html!';

@Component( {
	selector: 'my-apps',
	template: template,
	directives: [ CORE_DIRECTIVES, CarbonAppThumbnailComponent ],
	providers: [ MyAppsService ]
} )
export default class MyAppsView {
	static parameters = [ [ ElementRef ], [ MyAppsService ], [ Router ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;

	myAppsService:MyAppsService;
	carbonApps:CarbonApp[] = [];

	constructor( element:ElementRef, myAppsService:MyAppsService, router:Router ) {
		this.element = element;
		this.myAppsService = myAppsService;
		this.router = router;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.myAppsService.getApps().then(
			( apps )=> {
				apps.forEach( app=> {
					this.carbonApps.push( app );
					//console.log( this.router );
					//this.router.parent.config( [
					//	{path: '/' + app.slug, component: CarbonAppView, as: app.name.replace( " ", "" ), data: {alias: name.replace( " ", "" ), displayName: app.name}}
					//] );
					//setTimeout( _ => {
					//	let route = {path: '/' + app.slug, component: CarbonAppView, as: app.safeName, data: {alias: app.safeName, displayName: app.name}};
					//	this.dynamicRouteConfigurator.addRoute( this.constructor, route );
					//	//this.appRoutes = this.getAppRoutes();
					//}, 1000 );
				} );
			}
		).then(
			()=> {
				console.log( this.carbonApps );
			}
		);
	}

	onActivate():void {

	}
}