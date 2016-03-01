import { Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { ROUTER_DIRECTIVES, Router, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import SidebarService from "./../../../components/sidebar/service/SidebarService";
import MyAppsService from "./../../service/MyAppsService";
import App from "./../App";

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'dashboard',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ],
	providers: [ MyAppsService ]
} )
export default class AppDashboardView {
	router:Router;
	routeParams:RouteParams;
	sidebarService:SidebarService;
	myAppsService:MyAppsService;

	element:ElementRef;
	$element:JQuery;
	app:App;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sidebarService:SidebarService, myAppsService:MyAppsService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sidebarService = sidebarService;
		this.myAppsService = myAppsService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}