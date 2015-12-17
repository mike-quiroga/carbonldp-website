import { Component, CORE_DIRECTIVES, Input, DynamicComponentLoader, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import ContentService from "./../../content/ContentService";
import BlogPost from './BlogPost';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'blog-post',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
	providers: [ ContentService ]
} )
export default class BlogPostView {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ], [ RouteParams ], [ ContentService ] ];

	router:Router;
	dcl:DynamicComponentLoader;
	routeParams:RouteParams;
	contentService:ContentService;

	element:ElementRef;
	$element:JQuery;

	blogPost:BlogPost = new BlogPost();
	postid:number;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, routeParams:RouteParams, contentService:ContentService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.routeParams = routeParams;
		this.contentService = contentService;

		this.postid = <number>this.routeParams.get( 'id' );

	}


	onActivate():void {
		if ( this.postid !== undefined && this.postid !== null ) {
			this.contentService.getPost( this.postid ).then(
				( post )=> {
					let fileName:string = post.filename;
					@Component( {
						selector: 'compiled-component',
						directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
						templateUrl: "/assets/blog-posts/" + fileName
					} )
					class CompiledComponent {
						static parameters = [ [ ElementRef ] ];
						element:ElementRef;

						constructor( element:ElementRef ) {
							this.element = element;
						}

						afterViewInit():void {
							this.evalJavascript();
						}

						evalJavascript():void {
							let scripts:any[] = this.element.nativeElement.querySelectorAll( ".script" );
							let i:number = 0, scriptLength = scripts.length;
							for ( i; i < scriptLength; i ++ ) {
								eval( scripts[ i ].textContent );
							}
						}
					}
					this.dcl.loadIntoLocation( CompiledComponent, this.element, 'content' );
					this.blogPost = post;
				}
			);
		}
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}
}