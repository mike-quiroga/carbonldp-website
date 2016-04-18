import {Component, ElementRef, Input} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {Router} from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import template from "./template.html!";

@Component( {
	selector: "explorer-component",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class ExplorerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() appContext:App.Context;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( "Explorer: %o", this.appContext );
	}

}
