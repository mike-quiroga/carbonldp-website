import {Component, Input, Output, EventEmitter} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {Router, ROUTER_DIRECTIVES} from "angular2/router";

import "semantic-ui/semantic";

import App from "./../../app/App";
import AppActionButtons from "./../app-action-buttons/AppActionButtons";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtons, ],
} )
export default class AppsListComponent {
	router:Router;
	@Input() apps:App[];
	@Output() deleteApp:EventEmitter<App> = new EventEmitter();

	headers:Header[] = [ {name: "Name", value: "name"}, {name: "Creation", value: "created"}, {name: "Modification Date", value: "modified"} ];
	sortedColumn:string = null;
	ascending:boolean = false;

	constructor( router:Router ) {
		this.router = router;
	}

	navigateTo( url:any[] ):void {
		this.router.navigate( url );
	}

	sortColumn( header:Header ):void {
		if ( this.sortedColumn === header.value ) this.ascending = ! this.ascending;
		this.sortedColumn = header.value;

		this.apps.sort( ( contextA, contextB ) => {
			if ( contextA.app[ this.sortedColumn ] > contextB.app[ this.sortedColumn ] ) return this.ascending ? - 1 : 1;
			if ( contextA.app[ this.sortedColumn ] < contextB.app[ this.sortedColumn ] ) return this.ascending ? 1 : - 1;
			return 0;
		} );
	}

	onDeleteApp( appContext:App ):void {
		this.deleteApp.emit( appContext );
	}
}
export interface Header {
	name:string;
	value:string;
}
