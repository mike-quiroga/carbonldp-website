import { Component, ElementRef, Input, DynamicComponentLoader, Type } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import "semantic-ui/semantic";

import ContentService from "app/content/ContentService";
import BlogPost from "./../blog-post/BlogPost";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "blog-post-thumbnail",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]
} )
export default class BlogPostThumbnailComponent {
	router:Router;
	dcl:DynamicComponentLoader;

	element:ElementRef;
	contentService:ContentService;
	isNewPost:boolean;

	@Input() blogPost:BlogPost;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, contentService:ContentService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.contentService = contentService;
	}

	ngOnInit():void {
		this.isNewPost = false;
		if ( this.blogPost.creationDate ) {
			this.blogPost.creationDate = new Date( Date.parse( this.blogPost.creationDate.toString() ) );
			this.isNewPost = this.blogPost.creationDate.getDay() == new Date().getDay();
		}

	}

	ngAfterViewInit():void {
		let excerpt:string = this.blogPost.excerpt;
		if ( !! this.blogPost.filename ) {
			@Component( {
				selector: "compiled-component",
				directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
				template: excerpt
			} )
			class CompiledComponent {
			}
			this.dcl.loadIntoLocation( CompiledComponent, this.element, "excerpt" );
		}
	}
}