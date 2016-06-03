import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router, RouteParams } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import SidebarService from "./../../../components/sidebar/service/SidebarService";
import App from "./../App";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class AppDashboardView {
	router:Router;
	routeParams:RouteParams;
	sidebarService:SidebarService;

	element:ElementRef;
	$element:JQuery;
	app:App;

	constructor( router:Router, element:ElementRef, routeParams:RouteParams, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.routeParams = routeParams;
		this.sidebarService = sidebarService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}