import { Component, Input, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import App from "./../app/App";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class AppsListComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() apps:App[];

	constructor( element:ElementRef, router:Router ) {
		this.element = element;
		this.router = router;
	}

	ngAfterViewInit():void {
		console.log( this.apps );
		this.$element = $( this.element.nativeElement );
	}

	navigateTo( url:any[] ):void {
		console.log( url );
		this.router.navigate( url );
	}
}
