import {Component, ElementRef, Host, Inject, forwardRef} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {Router} from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import AppDetailView from "./../AppDetailView";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";
import DocumentExplorerComponent from "./document-explorer/DocumentExplorerComponent";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ CORE_DIRECTIVES, SPARQLClientComponent, DocumentExplorerComponent, ],
} )

export default class ExplorerView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	appContext:App.Context;
	private errorsAreaService:ErrorsAreaService;

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
		this.errorsAreaService.addError(
			error.title,
			error.content,
			error.statusCode,
			error.statusMessage,
			error.endpoint
		);
	}

}
