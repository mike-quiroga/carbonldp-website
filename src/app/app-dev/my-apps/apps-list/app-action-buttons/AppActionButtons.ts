import {Component, Input, Output, EventEmitter} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {Router, ROUTER_DIRECTIVES} from "angular2/router";

import "semantic-ui/semantic";

import App from "./../../app/App";

import template from "./template.html!";

@Component( {
	selector: "app-action-buttons",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class AppActionButtons {
	router:Router;
	@Input() app:App;
	@Output() deleteApp:EventEmitter<App> = new EventEmitter();

	constructor( router:Router ) {
		this.router = router;
	}

	onDeleteApp( event:Event ):void {
		event.stopPropagation();
		this.deleteApp.emit( this.app );
	}
}
