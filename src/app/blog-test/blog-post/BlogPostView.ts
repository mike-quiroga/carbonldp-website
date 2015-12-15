import { Component, CORE_DIRECTIVES, Input, DynamicComponentLoader, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import BlogService from "./../service/BlogService";
import BlogPost from './BlogPost';
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
//import template from './template.html!';

@Component( {
	selector: 'blog-post',
	template: `
		<article class="ui basic center aligned segment">
			<h1>{{ blogPost.title }}</h1>
			<div class="ui horizontal divider">
				{{ blogPost.creationDate }} by {{ blogPost.author }}
			</div>
			<div class="ui basic left aligned segment blog_content">
				<div #content></div>
			</div>
		</article>
	`,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]
} )
export default class BlogPostView {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ], [ BlogService ] ];

	router:Router;
	dcl:DynamicComponentLoader;

	element:ElementRef;
	$element:JQuery;
	blogService:BlogService;

	@Input() blogPost:BlogPost;
	cmvalue = "";

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, blogService:BlogService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.blogService = blogService;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		let turl:string = this.blogPost.filename;
		@Component( {
			selector: 'compiled-component',
			directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
			templateUrl: 'assets/blog-posts/' + turl
		} )
		class CompiledComponent {
		}
		this.dcl.loadIntoLocation( CompiledComponent, this.element, 'content' );
	}
}