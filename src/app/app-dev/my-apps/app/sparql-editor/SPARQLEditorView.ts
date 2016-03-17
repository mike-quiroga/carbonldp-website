/// <reference path="./../../../../../../typings/typings.d.ts" />
import { Component, ElementRef, Host, Inject, forwardRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbon/App";
import AppDetailView from "./../AppDetailView";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";
import {Message} from "app/app-dev/components/errors-area/ErrorsAreaComponent";

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
	private errorsAreaService:ErrorsAreaService

	constructor( router:Router, element:ElementRef, errorsAreaService:ErrorsAreaService, @Host() @Inject( forwardRef( () => AppDetailView ) )appDetail:AppDetailView ) {
		this.router = router;
		this.element = element;
		this.appContext = appDetail.appContext;
		this.errorsAreaService = errorsAreaService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	notifyErrorAreaService( error:any ):void {
		console.log( error );
		this.errorsAreaService.addError(
			error.name,
			error.message,
			error.response.status,
			error.response.request.statusText,
			error.response.request.responseURL
		);
	}

}
