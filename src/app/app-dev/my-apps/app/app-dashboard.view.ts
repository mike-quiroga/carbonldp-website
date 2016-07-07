import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router, RouteParams } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "./app";

import template from "./app-dashboard.view.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export class AppDashboardView {
	router:Router;
	routeParams:RouteParams;

	element:ElementRef;
	$element:JQuery;
	app:App.Class;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}
}

export default AppDashboardView;
