import { Component, CORE_DIRECTIVES, Input, DynamicComponentLoader, ElementRef, ComponentRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import 'semantic-ui/semantic';


import BlogPost from './../blog-post/BlogPost';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'blog-post-thumbnail',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]
} )
export default class BlogPostThumbnailComponent {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ]];

	router:Router;
	dcl:DynamicComponentLoader;

	element:ElementRef;
	isNewPost:boolean;

	@Input() blogPost:BlogPost;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;

	}

	onInit():void {
		this.isNewPost = false;
		if ( this.blogPost.creationDate ) {
			this.blogPost.creationDate = new Date( Date.parse( this.blogPost.creationDate.toString() ) );
			this.isNewPost = this.blogPost.creationDate.getDay() == new Date().getDay();
		}

	}

	afterViewInit():void {
		let excerpt:string = this.blogPost.excerpt;
		if ( !! this.blogPost.filename ) {
			@Component( {
				selector: 'compiled-component',
				directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
				template: excerpt
			} )
			class CompiledComponent {
			}
			this.dcl.loadIntoLocation( CompiledComponent, this.element, 'excerpt' );
		}
	}
}