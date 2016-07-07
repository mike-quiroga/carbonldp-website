import { Component, Host, Inject, forwardRef } from "@angular/core";

import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import { AppDetailView } from "./../app-detail.view";
import AppConfigurationComponent from "./app-configuration/AppConfigurationComponent";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ AppConfigurationComponent ],
} )

export default class ConfigurationView {
	appContext:App.Context;

	constructor( @Host() @Inject( forwardRef( () => AppDetailView ) ) appDetail:AppDetailView ) {
		this.appContext = appDetail.app.context;
	}

}
