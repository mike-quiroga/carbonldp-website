import { Component, DynamicComponentLoader, ElementRef, Type } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";

import $ from "jquery";
import "semantic-ui/semantic";

import BlogService from "./service/blog.service";
import BlogPost from "./blog-post/blog-post";
import BlogPostThumbnailComponent from "./blog-post-thumbnail/blog-post-thumbnail.component";
// import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";
import template from "./blog.view.html!";
import style from "./blog.view.css!text";

@Component( {
	selector: "blog",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostThumbnailComponent, CodeMirrorComponent.Class ],
	providers: [ BlogService, Title ],
	styles: [ style ],
} )
export default class BlogView {
	router:Router;
	dcl:DynamicComponentLoader;
	title:Title;

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
		this.title.setTitle( "Blog" );
	}
}