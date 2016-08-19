import { Component, ElementRef, Input, DynamicComponentLoader, OnInit, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, Router } from "@angular/router-deprecated";

import BlogPost from "./../blog-post/blog-post";

import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";

import "semantic-ui/semantic";

import template from "./blog-post-thumbnail.component.html!";
import style from "./blog-post-thumbnail.component.css!text";

@Component( {
	selector: "blog-post-thumbnail",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
	styles: [ style ],
} )
export class BlogPostThumbnailComponent implements OnInit, AfterViewInit {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ] ];

	private router:Router;
	dcl:DynamicComponentLoader;

	private element:ElementRef;
	private isNewPost:boolean;

	@Input() blogPost:BlogPost;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;

	}

	ngOnInit():void {
		this.isNewPost = false;
		if( this.blogPost.creationDate ) {
			this.blogPost.creationDate = new Date( Date.parse( this.blogPost.creationDate.toString() ) );
			this.isNewPost = this.blogPost.creationDate.getDay() == new Date().getDay();
		}

	}

	ngAfterViewInit():void {
		let excerpt:string = this.blogPost.excerpt;
		if( ! ! this.blogPost.filename ) {
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

export default BlogPostThumbnailComponent;