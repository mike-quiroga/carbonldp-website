import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import { App } from "./../app/app";
import AppActionButtons from "./app-action-buttons/AppActionButtons";

import template from "./apps-list.component.html!";
import "./apps-list.component.css!";

@Component( {
	selector: "apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtons, ],
} )
export class AppsListComponent {
	@Input() apps:App[];
	@Output() openApp:EventEmitter<App> = new EventEmitter<App>();
	@Output() deleteApp:EventEmitter<App> = new EventEmitter();

	headers:Header[] = [ { name: "Name", value: "name" }, { name: "Creation", value: "created" }, { name: "Modification Date", value: "modified" } ];
	sortedColumn:string = null;
	ascending:boolean = false;

	private router:Router;

	constructor( router:Router ) {
		this.router = router;
	}

	navigateTo( url:any[] ):void {
		this.router.navigate( url );
	}

	sortColumn( header:Header ):void {
		if( this.sortedColumn === header.value ) this.ascending = ! this.ascending;
		this.sortedColumn = header.value;

		this.apps.sort( ( contextA, contextB ) => {
			if( contextA.appContext[ this.sortedColumn ] > contextB.appContext[ this.sortedColumn ] ) return this.ascending ? - 1 : 1;
			if( contextA.appContext[ this.sortedColumn ] < contextB.appContext[ this.sortedColumn ] ) return this.ascending ? 1 : - 1;
			return 0;
		} );
	}

	onOpenApp( appContext:App ):void {
		this.openApp.emit( appContext );
	}

	onDeleteApp( appContext:App ):void {
		this.deleteApp.emit( appContext );
	}
}

export interface Header {
	name:string;
	value:string;
}

export default AppsListComponent;
