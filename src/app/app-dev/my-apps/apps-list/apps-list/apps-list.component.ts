import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import * as App from "../../app/app";
import AppActionButtonsComponent from "../app-action-buttons/app-action-buttons.component";

import template from "./apps-list.component.html!";
import "./apps-list.component.css!";

@Component( {
	selector: "apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, AppActionButtonsComponent, ],
} )
export class AppsListComponent {
	@Input() apps:App.Class[];
	@Output() openApp:EventEmitter<App.Class> = new EventEmitter<App.Class>();
	@Output() deleteApp:EventEmitter<App.Class> = new EventEmitter<App.Class>();

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

		this.apps.sort( ( appA, appB ) => {
			if( appA[ this.sortedColumn ] > appB[ this.sortedColumn ] ) return this.ascending ? - 1 : 1;
			if( appA[ this.sortedColumn ] < appB[ this.sortedColumn ] ) return this.ascending ? 1 : - 1;
			return 0;
		} );
	}

	onOpenApp( appContext:App.Class ):void {
		this.openApp.emit( appContext );
	}

	onDeleteApp( appContext:App.Class ):void {
		this.deleteApp.emit( appContext );
	}
}

export interface Header {
	name:string;
	value:string;
}

export default AppsListComponent;
