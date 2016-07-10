import { Component, Host, Inject, forwardRef } from "@angular/core";

import * as App from "carbonldp/App";

import { AppDetailView } from "./../app-detail.view";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
import { ErrorsAreaService } from "carbon-panel/errors-area/errors-area.service";

import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ SPARQLClientComponent, ],
} )

export class SPARQLEditorView {
	$element:JQuery;
	appContext:App.Context;
	private errorsAreaService:ErrorsAreaService;

	constructor( errorsAreaService:ErrorsAreaService, @Host() @Inject( forwardRef( () => AppDetailView ) ) appDetail:AppDetailView ) {
		this.appContext = appDetail.app.context;
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
