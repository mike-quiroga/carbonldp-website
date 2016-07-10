import { Component, ElementRef, Input, Output, EventEmitter } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";
import "jstree";

import * as Pointer from "carbonldp/Pointer";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as HTTP from "carbonldp/HTTP";
import * as URI from "carbonldp/RDF/URI";
import * as SDKContext from "carbonldp/SDKContext";
import * as RDFDocument from "carbonldp/RDF/Document";

import template from "./document-tree-view.component.html!";
import style from "./document-tree-view.component.css!text";

@Component( {
	selector: "document-treeview",
	template: template,
	styles: [ style ],
} )

export default class DocumentTreeViewComponent {
	element:ElementRef;
	$element:JQuery;

	documentTree:JQuery;
	nodeChildren:any[] = [];

	@Input() documentContext:SDKContext.Class;
	@Output() onResolveUri:EventEmitter<RDFDocument.Class> = new EventEmitter<RDFDocument.Class>();
	@Output() onError:EventEmitter<HTTP.Errors.Error> = new EventEmitter<HTTP.Errors.Error>();
	@Output() onLoadingDocument:EventEmitter<boolean> = new EventEmitter<boolean>();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnInit():void {
		let head:Element = document.querySelector( "head" );
		let link:HTMLLinkElement = document.createElement( "link" );
		link.rel = "stylesheet";
		link.href = "assets/node_modules/jstree/dist/themes/default/style.min.css";
		head.appendChild( link );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.documentTree = this.$element.find( ".document.treeview" );
		this.onLoadingDocument.emit( true );
		this.getDocumentTree().then( ()=> {
			this.renderTree();
			this.onLoadingDocument.emit( false );
		} );
	}

	getDocumentTree():Promise<PersistedDocument.Class> {
		return this.documentContext.documents.get( "" ).then( ( [ resolvedRoot, response ]:[ PersistedDocument.Class, HTTP.Response.Class ] ) => {
			resolvedRoot.contains.forEach( ( pointer:Pointer.Class ) => {
				this.nodeChildren.push( this.buildNode( pointer.id ) );
			} );
			return resolvedRoot;
		} ).catch( ( error:HTTP.Errors.Error ) => {
			console.error( "Error:%o", error );
			this.onError.emit( error );
		} );
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
		let $children:JQuery = this.documentTree.jstree( true ).get_children_dom( nodeId );
		let childElements:Element[] = jQuery.makeArray( $children );
		while ( childElements.length > 0 ) {
			this.documentTree.jstree( true ).delete_node( childElements[ 0 ] );
			childElements.splice( 0, 1 );
		}
	}

	onBeforeOpenNode( parentId:string, parentNode:any, position:string ):void {
		let oldIcon:string = parentNode.icon;
		let $documentTree:JSTree = this.documentTree.jstree( true );

		$documentTree.set_icon( parentNode, $documentTree.settings.types.loading.icon );
		this.getNodeChildren( parentNode.data.pointer.id ).then( ( children:any[] ):void => {
			this.emptyNode( parentId );
			if( children.length > 0 ) {
				children.forEach( ( childNode:any ) => this.addChild( parentId, childNode, position ) );
			}
		} ).then( () => {
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
		return this.documentContext.documents.get( uri ).then( ( [resolvedRoot, response]:[PersistedDocument.Class, HTTP.Response.Class] ) => {
			if( ! resolvedRoot.contains ) return [];

			return resolvedRoot.contains.map( ( pointer:Pointer.Class ):void => {
				return this.buildNode( pointer.id );
			} );
		} ).catch( ( error ) => {
			console.log( "Error: %o", error );
		} );
	}


	getSlug( pointer:Pointer.Class | string ):string {
		if( typeof pointer !== "string" ) return ( <Pointer.Class>pointer ).id;

		return URI.Util.getSlug( <string>pointer );
	}

	private removeTrailingSlash( slug:string ):string {
		if( slug.endsWith( "/" ) ) {
			return slug.substr( 0, slug.length - 1 );
		} else {
			return slug;
		}
	}
}
