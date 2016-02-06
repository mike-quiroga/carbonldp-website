import { Component, DynamicComponentLoader, ElementRef, Type } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import BlogService from "./service/BlogService";
import BlogPost from "./blog-post/BlogPost";
import BlogPostThumbnailComponent from "./blog-post-thumbnail/BlogPostThumbnailComponent";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'blog',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostThumbnailComponent, CodeMirrorComponent.Class ],
	providers: [ BlogService ]
} )
export default class BlogView {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ], [ BlogService ] ];

	router:Router;
	dcl:DynamicComponentLoader;

	blogService:BlogService;

	element:ElementRef;
	$element:JQuery;

	blogPosts:BlogPost[];

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, blogService:BlogService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.blogService = blogService;
	}

	getPostsList():void {
		this.blogService.getPostsList().then(
			( blogPosts ) => {
				this.blogPosts = blogPosts;
			}
		).catch( console.error );
	}


	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	routerOnActivate():void {
		this.getPostsList();
	}
}