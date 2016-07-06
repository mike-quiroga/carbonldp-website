import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import { App } from "./../app/app";
import AppActionButtons from "./app-action-buttons/AppActionButtons";

import template from "./app-tile.component.html!";
import "./app-tile.component.css!";

@Component( {
	selector: "app-tile",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtons ],
} )
export default class AppTileComponent {
	@Input() app:App;
	@Output() openApp:EventEmitter<App> = new EventEmitter<App>();
	@Output() deleteApp:EventEmitter<App> = new EventEmitter<App>();

	onOpenApp( appContext:App ):void {
		this.openApp.emit( appContext );
	}

	onDeleteApp( appContext:App ):void {
		this.deleteApp.emit( appContext );
	}
}
