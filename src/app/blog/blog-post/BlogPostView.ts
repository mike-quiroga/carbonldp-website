import { Component, CORE_DIRECTIVES, Input, DynamicComponentLoader, ElementRef, Type } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import BlogService from "./../service/BlogService";
import BlogPost from './BlogPost';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'blog-post',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
	providers: [ BlogService ]
} )
export default class BlogPostView {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ], [ RouteParams ], [ BlogService ] ];

	router:Router;
	dcl:DynamicComponentLoader;
	routeParams:RouteParams;
	blogService:BlogService;

	element:ElementRef;
	$element:JQuery;

	blogPost:BlogPost = new BlogPost();
	postid:number;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, routeParams:RouteParams, blogService:BlogService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.routeParams = routeParams;
		this.blogService = blogService;

		this.postid = <number>this.routeParams.get( 'id' );

	}


	onActivate():void {
		if ( typeof this.postid === "undefined" || this.postid === null ) {
			return;
		} else {
			this.blogService.getPost( this.postid ).then(
				( post )=> {
					let fileName:string = post.filename;
					this.createPostComponent( fileName );
					this.blogPost = post;
				}
			);
		}
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	private createPostComponent( fileName:string ):Type {
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

			}
		}
		this.dcl.loadIntoLocation( CompiledComponent, this.element, 'content' );
	}
}