import { Component, DynamicComponentLoader, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import BlogService from "./service/blog.service";
import BlogPost from "./blog-post/blog-post";
import BlogPostThumbnailComponent from "./blog-post-thumbnail/blog-post-thumbnail.component";

import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";
import template from "./blog.view.html!";
import style from "./blog.view.css!text";

@Component( {
	selector: "blog",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostThumbnailComponent, CodeMirrorComponent.Class ],
	providers: [ BlogService ],
	styles: [ style ],
} )
export default class BlogView {
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

	routerOnActivate():void {
		this.getPostsList();
	}
}