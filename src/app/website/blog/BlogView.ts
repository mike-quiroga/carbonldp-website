import { Component, DynamicComponentLoader, ElementRef, Type } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";
import { Title } from "angular2/platform/browser";

import $ from "jquery";
import "semantic-ui/semantic";

import BlogService from "./service/BlogService";
import BlogPost from "./blog-post/BlogPost";
import BlogPostThumbnailComponent from "./blog-post-thumbnail/BlogPostThumbnailComponent";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "blog",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostThumbnailComponent, CodeMirrorComponent.Class ],
	providers: [ BlogService, Title ]
} )
export default class BlogView {
	router:Router;
	dcl:DynamicComponentLoader;
	title: Title;

	blogService:BlogService;

	element:ElementRef;
	$element:JQuery;

	blogPosts:BlogPost[];

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, blogService:BlogService, title:Title ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.blogService = blogService;
		this.title = title;
		this.title.setTitle( "Blog" );
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