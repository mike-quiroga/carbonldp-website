import {Component, ElementRef, Input, SimpleChange} from "angular2/core";
import {CORE_DIRECTIVES, NgSwitch, NgSwitchWhen, NgSwitchDefault} from "angular2/common";
import {Router} from "angular2/router";

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
import * as Value from "carbonldp/RDF/Value";
import * as Literal from "carbonldp/RDF/Literal";
import * as List from "carbonldp/RDF/List";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";

import AppContextService from "./../../../../../AppContextService";
import HighlightDirective from "./../../../../../../directives/HighlightDirective";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "jsonld-viewer",
	template: template,
	directives: [ CORE_DIRECTIVES, NgSwitchDefault, HighlightDirective ],
} )

export default class JsonldViewerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() document:RDFDocument.Class;
	appContextService:AppContextService;

	rootNode:RDFNode.Class;

	constructor( router:Router, element:ElementRef, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.appContextService = appContextService;
	}

	ngAfterViewInit():void {
		console.log( "Viewer: %o", this.document );
		this.$element = $( this.element.nativeElement );
		this.initializeTabs();
	}

	ngOnChanges( changes:{[propName:string]:SimpleChange} ):void {
		if ( changes[ "document" ].currentValue !== changes[ "document" ].previousValue ) {
			console.log( this.document );
			let documents:RDFNode.Class[] = RDFDocument.Util.getDocumentResources( this.document );
			this.rootNode = documents[ 0 ];
			console.log( this.rootNode );
			this.initializeTabs();
		}
	}

	getDisplayName( uri:string ):string {
		if ( uri === "@id" || uri === "@type" )
			return uri;
		if ( URI.Util.hasFragment( uri ) )
			return this.getFragment( uri );
		return URI.Util.getSlug( uri );
	}

	getProperties( document:RDFNode.Class ):Value[] {
		let properties:Value[] = [];
		for ( let property in document ) {
			if ( ! document.hasOwnProperty( property ) ) continue;
			let prop:Object = {};
			prop[ property ] = document[ property ];
			properties.push( prop );
		}
		return properties;
	}

	getPropertyNames( property:any ):string[] {
		return Object.keys( property );
	}

	getFragment( uri:string ):string {
		return URI.Util.getFragment( uri );
	}

	getTypeOf( property:any ):string {
		return typeof property;
	}

	isLiteral( property:any ):boolean {
		return Literal.Factory.is( property );
	}

	isArray( property:any ):boolean {
		return Utils.isArray( property );
	}

	isUrl( uri:string ):boolean {
		let regexp:RegExp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)? (\/|\/([\w#!:.?+=&%@!\-\/]))?/;
		return regexp.test( uri );
	}

	initializeTabs():void {
		if ( this.$element ) this.$element.find( ".tabular.menu .item" ).tab();
	}
}
