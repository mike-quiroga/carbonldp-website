import { Component, Input, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import App from "./../app/App";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "apps-list",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class AppsListComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() apps:App[];

	headers:Header[] = [ {name: "Name", value: "name"}, {name: "Creation", value: "created"}, {name: "Modification Date", value: "modified"} ];
	sortedColumn:string = null;
	ascending:boolean = false;

	constructor( element:ElementRef, router:Router ) {
		this.element = element;
		this.router = router;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
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

}
export interface Header {
	name:string;
	value:string;
}
