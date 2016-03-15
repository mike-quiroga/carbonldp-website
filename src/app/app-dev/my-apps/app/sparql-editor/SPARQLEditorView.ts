/// <reference path="./../../../../../../typings/typings.d.ts" />
import { Component, ElementRef, Host, Inject, forwardRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbon/App";
import AppDetailView from "./../AppDetailView";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ CORE_DIRECTIVES, SPARQLClientComponent, ],
} )

export default class SPARQLEditorView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	appContext:App.Context;
	app:App;

	constructor( router:Router, element:ElementRef, @Host() @Inject( forwardRef( () => AppDetailView ) )appDetail:AppDetailView ) {
		this.router = router;
		this.element = element;
		this.appContext = appDetail.appContext;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
