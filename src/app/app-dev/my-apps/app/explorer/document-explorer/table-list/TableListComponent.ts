import { Component, ElementRef, Input, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES, NgSwitch, NgSwitchWhen, NgSwitchDefault } from "angular2/common";
import { Router } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Documents from "carbonldp/Documents";
import * as Pointer from "carbonldp/Pointer";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as Request from "carbonldp/HTTP/Request";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as NS from "carbonldp/NS";
import * as RDFDocument from "carbonldp/RDF/Document";
import * as RDFNode from "carbonldp/RDF/RDFNode";
import * as RDFValue from "carbonldp/RDF/Value";
import * as Literal from "carbonldp/RDF/Literal";
import * as List from "carbonldp/RDF/List";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import AppContextService from "./../../../../../AppContextService";
import HighlightDirective from "./../../../../../../directives/HighlightDirective";

import template from "./template.html!";
// import "./style.css!";
import { ajaxGetJSON } from "rxjs/observable/dom/AjaxObservable";

@Component( {
	selector: "table-list",
	template: template,
	directives: [ CORE_DIRECTIVES, NgSwitchDefault, HighlightDirective ],
} )

export default class TableListComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() bNodesDictionary:Map<string,RDFNode.Class>;
	@Input() itemsToPrint:any[];
	headers:string[] = [];

	rootNode:RDFNode.Class;

	constructor( router:Router, element:ElementRef, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
	}

	ngOnInit():void {
		this.headers = this.getHeaders();
	}

	ngAfterViewInit():void {
		console.log( "bNodes Map: %o", this.bNodesDictionary );
		console.log( "Items to Print: %o", this.itemsToPrint );
		this.$element = $( this.element.nativeElement );
	}

	getHeaders():string[] {
		return Object.keys( this.bNodesDictionary.get( this.itemsToPrint[ 0 ][ "@id" ] ) );
	}

	getItemProperties( item:any ):string[] {
		return Object.keys( this.bNodesDictionary.get( item[ "@id" ] ) );
	}

	getBNode( item:any ):any {
		return this.bNodesDictionary.get( item[ "@id" ] )
	}

	getJson( obj:any ):string {
		return JSON.stringify( obj, null, 2 );
	}
}
