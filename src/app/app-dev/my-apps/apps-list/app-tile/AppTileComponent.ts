import {Component, Input, Output, EventEmitter} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {ROUTER_DIRECTIVES} from "angular2/router";

import "semantic-ui/semantic";

import App from "../../app/App";
import AppActionButtons from "./../app-action-buttons/AppActionButtons";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "app-tile",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtons ],
} )
export default class AppTileComponent {
	@Input() app:App;
	@Output() deleteApp:EventEmitter<App> = new EventEmitter();

	onDeleteApp( appContext:App ):void {
		this.deleteApp.emit( appContext );
	}
}
