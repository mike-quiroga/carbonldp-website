import { Component, Host, Inject, forwardRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import "semantic-ui/semantic";

import * as App from "carbonldp/App";

import AppDetailView from "./../AppDetailView";
import AppConfigurationComponent from "app/app-dev/components/app-configuration/AppConfigurationComponent";

import template from "./template.html!";

@Component( {
	selector: "dashboard",
	template: template,
	directives: [ CORE_DIRECTIVES, AppConfigurationComponent ],
} )

export default class ConfigurationView {
	appContext:App.Context;

	constructor( @Host() @Inject( forwardRef( () => AppDetailView ) )appDetail:AppDetailView ) {
		this.appContext = appDetail.appContext;
	}

}
