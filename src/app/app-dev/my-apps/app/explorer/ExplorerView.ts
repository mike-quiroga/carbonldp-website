import { Component, Host, Inject, forwardRef } from "@angular/core";

import "semantic-ui/semantic";

import * as App from "./../app";
import { AppDetailView } from "./../app-detail.view";
import DocumentExplorerComponent from "./document-explorer/DocumentExplorerComponent";
import template from "./template.html!";

@Component( {
	selector: "explorer-view",
	template: template,
	directives: [ DocumentExplorerComponent, ],
} )

export default class ExplorerView {
	app:App.Class;

	constructor( @Host() @Inject( forwardRef( () => AppDetailView ) ) appDetail:AppDetailView ) {
		this.app = appDetail.app;
	}
}
