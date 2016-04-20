import {Component, ElementRef, Input} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
import {Router} from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import * as Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Pointer from "carbonldp/Pointer";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as URI from "carbonldp/RDF/URI";
import * as Context from "carbonldp/Context";

import AppContextService from "./../../../../AppContextService";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "explorer-component",
	template: template,
	directives: [ CORE_DIRECTIVES, ],
} )

export default class ExplorerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() appContext:App.Context;
	appContextService:AppContextService;

	members:any[] = [];
	contains:any[];

	constructor( router:Router, element:ElementRef, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.appContextService = appContextService;
	}

	ngAfterContentInit():void {
		this.$element = $( this.element.nativeElement );
		console.log( "Explorer: %o", this.appContext );
		console.log( "Explorer: %o", this.appContext.app.rootContainer.isResolved() );
		// this.appContext.app.rootContainer.resolve().then();
		this.appContext.documents.get( "" ).then(
			( [resolvedRoot, response]:[PersistedDocument.Class, HTTP.Response.Class] ) => {
				console.log( "Resolved root: %o", resolvedRoot );
				resolvedRoot.members.forEach(
					( member:Pointer.Class ) => {
						this.members.push( this.getSlug( member ) );
					}
				);
				console.log();
			},
			( error ) => {
				console.log( "Error:%o", error );
			}
		);
		this.refreshAccordion();
	}

	refreshAccordion():void {
		let self:any = this;
		this.$element.find( ".ui.accordion" ).accordion( {
			onChange: function ():void {
				self.toggleItem( this );
			},
			exclusive: false,
		} );
	}

	toggleItem( trigger:HTMLElement ):void {
		$( trigger ).closest( ".item" ).toggleClass( "active" );
	}

	getSlug( pointer:Pointer.Class ):string {
		return this.removeTrailingSlash( URI.Util.getSlug( pointer.id ) );
	}

	private removeTrailingSlash( slug:string ):string {
		if ( slug.endsWith( "/" ) ) {
			return slug.substr( 0, slug.length - 1 );
		} else {
			return slug;
		}
	}
}
