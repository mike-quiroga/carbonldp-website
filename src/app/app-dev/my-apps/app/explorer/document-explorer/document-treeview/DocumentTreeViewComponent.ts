import { Component, ElementRef, Input, Output, EventEmitter } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";
import "jstree/dist/themes/default/style.min.css!";

import * as Pointer from "carbonldp/Pointer";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";

import template from "./template.html!";
// import "./style.css!";

@Component( {
	selector: "document-treeview",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )

export default class DocumentTreeViewComponent {
	element:ElementRef;
	$element:JQuery;
	@Input() documentContext:SDKContext.Class;

	documentTree:JQuery;
	nodeChildren:any[] = [];
	@Output() onResolveUri:EventEmitter<RDFDocument.Class> = new EventEmitter();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		console.log( "TreeView: %o", this.documentContext );
		this.$element = $( this.element.nativeElement );
		this.documentTree = this.$element.find( ".document.treeview" );
		this.createTree();
	}

	createTree():void {
		// TODO: use this.documentContext.app.rootContainer.resolve() when 401 error is fixed on SDK
		let rootNodes:Promise<[PersistedDocument.Class, HTTP.Response.Class]> = this.documentContext.documents.get( "" );
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
				this.renderTree();
			}
		);
	}

	buildNode( uri:string ):any {
		return {
			"text": this.getSlug( uri ),
			"state": { "opened": false },
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
		this.documentTree.jstree( {
			"core": {
				"data": this.nodeChildren,
				"check_callback": true,
			},
			"types": {
				"default": {
					"icon": "file outline icon",
				},
				"loading": {
					"icon": "spinner loading icon",
				},
			},
			"plugins": [ "types", "wholerow" ],
		} );
		this.documentTree.jstree();
		this.documentTree.on( "create_node.jstree", ( e:Event, data:any ):void => {} );
		this.documentTree.on( "before_open.jstree", ( e:Event, data:any ):void => {
			let parentId:any = data.node.id;
			let parentNode:any = data.node;
			let position:string = "last";
			this.onBeforeOpenNode( parentId, parentNode, position );
		} );
		this.documentTree.on( "changed.jstree", ( e:Event, data:any ):void => {
			let parentId:any = data.node.id;
			let parentNode:any = data.node;
			let position:string = "last";
			this.onClickNode( parentId, parentNode, position );
		} );
	}

	emptyNode( nodeId:string ):void {
		let children:JQuery|JQuery[] = this.documentTree.jstree( true ).get_children_dom( nodeId );
		while ( children.length > 0 ) {
			this.documentTree.jstree( true ).delete_node( children[ 0 ] );
			(<JQuery[]>children).splice( 0, 1 );
		}
	}

	onBeforeOpenNode( parentId:string, parentNode:any, position:string ):void {
		let oldIcon:string = parentNode.icon;
		let $documentTree:JSTree = this.documentTree.jstree( true );

		$documentTree.set_icon( parentNode, $documentTree.settings.types.loading.icon );
		this.getNodeChildren( parentNode.data.pointer.id ).then(
			( children:any[] ):void => {
				this.emptyNode( parentId );
				if ( children.length > 0 ) {
					children.forEach( ( childNode:any ) => this.addChild( parentId, childNode, position ) );
				}
			}
		).then( () => {
			$documentTree.set_icon( parentNode, oldIcon );
		} );
	}

	onClickNode( parentId:string, node:any, position:string ):void {
		this.onResolveUri.emit( node.data.pointer.id );
	}

	addChild( parentId:string, node:any, position:string ):void {
		this.documentTree.jstree( true ).create_node( parentId, node, position );
	}

	getNodeChildren( uri:string ):Promise<any[]> {
		return this.documentContext.documents.get( uri ).then(
			( [resolvedRoot, response]:[PersistedDocument.Class, HTTP.Response.Class] ) => {
				console.log( "documents.get('" + uri + "') -->  %o", resolvedRoot );
				let children:any[] = [];
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
