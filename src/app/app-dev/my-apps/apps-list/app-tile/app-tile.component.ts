import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import * as App from "../../app/app";
import { AppActionButtonsComponent } from "../app-action-buttons/app-action-buttons.component";

import template from "./app-tile.component.html!";
import "./app-tile.component.css!";

@Component( {
	selector: "app-tile",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtonsComponent ],
} )
export class AppTileComponent {
	@Input() app:App.Class;
	@Output() openApp:EventEmitter<App.Class> = new EventEmitter<App.Class>();
	@Output() deleteApp:EventEmitter<App.Class> = new EventEmitter<App.Class>();

	onOpenApp( app:App.Class ):void {
		this.openApp.emit( app );
	}

	onDeleteApp( app:App.Class ):void {
		this.deleteApp.emit( app );
	}
}

export default AppTileComponent;
