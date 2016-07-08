import { Component, Host, Inject, forwardRef } from "@angular/core";

import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import AppDetailView from "./../AppDetailView";
import DocumentExplorerComponent from "./document-explorer/document-explorer.component";
import template from "./template.html!";

@Component( {
	selector: "explorer-view",
	template: template,
	directives: [ DocumentExplorerComponent, ],
} )

export default class ExplorerView {
	appContext:App.Context;

	constructor( @Host() @Inject( forwardRef( () => AppDetailView ) )appDetail:AppDetailView ) {
		this.appContext = appDetail.appContext;
	}
}