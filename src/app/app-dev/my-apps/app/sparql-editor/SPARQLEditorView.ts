import { Component, Host, Inject, forwardRef } from "@angular/core";

import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import AppDetailView from "./../AppDetailView";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ SPARQLClientComponent, ],
} )

export default class SPARQLEditorView {
	$element:JQuery;
	appContext:App.Context;
	private errorsAreaService:ErrorsAreaService;

	constructor( errorsAreaService:ErrorsAreaService, @Host() @Inject( forwardRef( () => AppDetailView ) )appDetail:AppDetailView ) {
		this.appContext = appDetail.appContext;
		this.errorsAreaService = errorsAreaService;
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
