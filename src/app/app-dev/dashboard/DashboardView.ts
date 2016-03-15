import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
//import "./style.css!";
import SidebarService from "../components/sidebar/service/SidebarService";

@Component( {
	selector: "home",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
} )
export default class DashboardView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	sidebarService:SidebarService;

	constructor( router:Router, element:ElementRef, sidebarService:SidebarService ) {
		this.router = router;
		this.element = element;
		this.sidebarService = sidebarService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );

	}

	routerOnDeactivate():void {

	}
}