import {Component, ElementRef, Input} from "angular2/core";
import {CORE_DIRECTIVES} from "angular2/common";
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

	appTree:JQuery;
	nodeChildren:any[] = [];
	members:any[] = [];
	contains:Pointer[] = [];
	loadingTree:boolean = false;

	documents:Documents;

	constructor( router:Router, element:ElementRef, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.appContextService = appContextService;
	}

	ngOnInit():void {
		this.createTree();
	}


	ngAfterViewInit():void {
		console.log( "Explorer: %o", this.appContext );
		this.$element = $( this.element.nativeElement );
		this.appTree = this.$element.find( ".app.treeview" );

	}

	createTree():void {
		// TODO: use this.appContext.app.rootContainer.resolve() when 401 error is fixed on SDK
		this.appContext.documents.get( "" ).then(
			( [resolvedRoot, response]:[PersistedDocument.Class, HTTP.Response.Class] ) => {
				console.log( "documents.get('') -->  %o", resolvedRoot );
				resolvedRoot.contains.forEach(
					( pointer:Pointer.Class ) => {
						this.nodeChildren.push( this.buildNode( pointer.id ) );
					}
				);
			},
			( error ) => {
				console.log( "Error:%o", error );
			}
		).then(
			():void => {
				this.loadingTree = false;
				this.renderTree();
			}
		);
	}

	buildNode( uri:string ):any {
		return {
			"text": this.getSlug( uri ),
			"state": {"opened": false},
			"children": [
				{
					"text": "No children...",
				},
			],
			"data": {
				"pointer": {
					"id": uri,
				},
			},
		};
	}

	renderTree():void {
		this.appTree.jstree( {
			"core": {
				"data": this.nodeChildren,
				"check_callback": true,
			},
			"types": {
				"default": {
					"icon": "folder outline icon",
				},
				"default": {
					"icon": "file outline icon",
				},
			},
			"plugins": [ "types", "wholerow" ],
		} );
		this.appTree.jstree();
		this.appTree.on( "create_node.jstree", ( e:Event, data:any ):void => {
			console.log( "saved" );
		} );
		this.appTree.on( "before_open.jstree", ( e:Event, data:any ):void => {
			console.log( data )
			let position:string = "last";
			let parentId:any = data.node.id;
			let parentNode:any = data.node;
			console.log( parentId );
			this.getNodeChildren( parentNode.data.pointer.id ).then(
				( children:any[] ):void => {
					console.log( "children: %o", children );
					let firstChild:any = this.appTree.jstree( true ).get_node( this.appTree.jstree( true ).get_children_dom( parentId )[ 0 ] );
					this.appTree.jstree( true ).delete_node( firstChild );
					if ( children.length > 0 ) {
						children.forEach( ( childNode:any ) => this.addChild( parentId, childNode, position ) );
					}
				}
			);
		} );
		this.appTree.on( "changed.jstree", ( e:Event, data:any ):void => {

		} );
	}

	emptyNode( nodeId:string ):void {
		let children:JQuery[] = this.appTree.jstree( true ).get_children_dom( nodeId );
		while ( children.length > 0 ) {
			this.appTree.jstree( true ).delete_node( children );
		}
	}

	removeChild( parentId:string, node:any ):void {

	}

	addChild( parentId:string, node:any, position:string ):void {
		console.log( "Add Child ( parentId: %o, newNode: %o, position: %o )", "#" + parentId, node, position );
		this.appTree.jstree( true ).create_node( parentId, node, position );
	}

	getNodeChildren( uri:string ):Promise<any[]> {
		let children:any[] = [];
		return this.appContext.documents.get( uri ).then(
			( [resolvedRoot, response]:[PersistedDocument.Class, HTTP.Response.Class] ) => {
				console.log( "documents.get('" + uri + "') -->  %o", resolvedRoot );
				if ( resolvedRoot.contains )
					resolvedRoot.contains.forEach(
						( pointer:Pointer.Class ):void => {
							children.push( this.buildNode( pointer.id ) );
						}
					);
				return children;
			},
			( error ) => {
				console.log( "Error:%o", error );
			}
		);
	}


	getSlug( pointer:Pointer.Class|string ):string {
		if ( typeof pointer !== "string" )
			return (<Pointer.Class>pointer).id;
		return URI.Util.getSlug( <string>pointer );
	}

	private removeTrailingSlash( slug:string ):string {
		if ( slug.endsWith( "/" ) ) {
			return slug.substr( 0, slug.length - 1 );
		} else {
			return slug;
		}
	}
}
