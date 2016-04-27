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
import * as Request from "carbonldp/HTTP/Request";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as NS from "carbonldp/NS";
import * as RDFDocument from "carbonldp/RDF/Document";

import AppContextService from "./../../../../AppContextService";

import JsonldViewer from "./document-viewer/JsonldViewer";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "document-explorer",
	template: template,
	directives: [ CORE_DIRECTIVES, JsonldViewer ],
} )

export default class DocumentExplorerComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	@Input() documentContext:SDKContext.Class;
	appContextService:AppContextService;

	appTree:JQuery;
	nodeChildren:any[] = [];
	members:any[] = [];
	contains:Pointer[] = [];
	loadingTree:boolean = false;
	loadingDocument:boolean = false;

	inspectingDocument:RDFDocument;

	constructor( router:Router, element:ElementRef, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.appContextService = appContextService;
	}

	ngAfterViewInit():void {
		console.log( "Explorer: %o", this.documentContext );
		this.$element = $( this.element.nativeElement );
		this.appTree = this.$element.find( ".app.treeview" );
		this.createTree();
	}

	createTree():void {
		// TODO: use this.documentContext.app.rootContainer.resolve() when 401 error is fixed on SDK
		let rootNodes:Promise = this.documentContext.documents.get( "" );
		rootNodes.then(
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
		);
		rootNodes.then(
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
					"text": "Loading...",
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
				"loading": {
					"icon": "spinner loading icon",
				},
			},
			"plugins": [ "types", "wholerow" ],
		} );
		this.appTree.jstree();
		this.appTree.on( "create_node.jstree", ( e:Event, data:any ):void => {} );
		this.appTree.on( "before_open.jstree", ( e:Event, data:any ):void => {
			let parentId:any = data.node.id;
			let parentNode:any = data.node;
			let position:string = "last";
			this.onBeforeOpenNode( parentId, parentNode, position );
		} );
		this.appTree.on( "changed.jstree", ( e:Event, data:any ):void => {
			let parentId:any = data.node.id;
			let parentNode:any = data.node;
			let position:string = "last";
			this.onClickNode( parentId, parentNode, position );
		} );
	}

	emptyNode( nodeId:string ):void {
		let children:JQuery[] = this.appTree.jstree( true ).get_children_dom( nodeId );
		while ( children.length > 0 ) {
			this.appTree.jstree( true ).delete_node( children[ 0 ] );
			children.splice( 0, 1 );
		}
	}

	onBeforeOpenNode( parentId:string, parentNode:any, position:string ):void {
		let oldIcon:string = parentNode.icon;
		let rootNode:any = this.appTree.jstree( true ).get_node( this.appTree.jstree( true ).element );
		let $appTree:JSTree = this.appTree.jstree( true );

		$appTree.set_icon( parentNode, $appTree.settings.types.loading.icon );
		this.getNodeChildren( parentNode.data.pointer.id ).then(
			( children:any[] ):void => {
				this.emptyNode( parentId );
				if ( children.length > 0 ) {
					children.forEach( ( childNode:any ) => this.addChild( parentId, childNode, position ) );
				}
			}
		).then( () => {
			$appTree.set_icon( parentNode, oldIcon );
		} );
	}

	onClickNode( parentId:string, node:any, position:string ):void {
		console.log( arguments );
		this.loadingDocument = true;
		let requestOptions:Request.Options = {
			sendCredentialsOnCORS: true,
		};
		if ( this.documentContext && this.documentContext.auth.isAuthenticated() ) this.documentContext.auth.addAuthentication( requestOptions );
		let parser:RDFDocument.Parser = new RDFDocument.Parser();
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.RDFSource, requestOptions );
		HTTP.Request.Service.get( node.data.pointer.id, requestOptions ).then(
			( response:HTTP.Response.Class ) => {
				console.log( "Returned node: %o", response );
				parser.parse( response.data ).then(
					( parsedDocument:RDFDocument ) => {
						console.log( "Parsed node: %o", parsedDocument );
						if ( parsedDocument[ 0 ] )
							this.inspectingDocument = (<RDFDocument>parsedDocument[ 0 ]);
					}
				);
			}
		).then(
			()=> {
				this.loadingDocument = false;
			}
		);
	}

	addChild( parentId:string, node:any, position:string ):void {
		this.appTree.jstree( true ).create_node( parentId, node, position );
	}

	getNodeChildren( uri:string ):Promise<any[]> {
		let children:any[] = [];
		return this.documentContext.documents.get( uri ).then(
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
