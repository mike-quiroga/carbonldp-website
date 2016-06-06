import { Component, Input, Output, EventEmitter } from "@angular/core";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import App from "./../../app/App";

import template from "./template.html!";

@Component( {
	selector: "app-action-buttons",
	template: template,
	directives: [ ROUTER_DIRECTIVES, ],
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

	avoidRowClick( event:Event ):void {
		event.stopPropagation();
	}
}
