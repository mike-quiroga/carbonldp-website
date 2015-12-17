import { Component, CORE_DIRECTIVES, DynamicComponentLoader, ElementRef, Type } from "angular2/angular2";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import ContentService from "./../content/ContentService";
import BlogPost from "./blog-post/BlogPost";
import BlogPostThumbnailView from "./blog-post-thumbnail/BlogPostThumbnailView";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'blog-test',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostThumbnailView, CodeMirrorComponent.Class ],
	providers: [ ContentService ]
} )
export default class BlogView {
	static parameters = [ [ Router ], [ ElementRef ], [ DynamicComponentLoader ], [ ContentService ] ];

	router:Router;
	dcl:DynamicComponentLoader;

	contentService:ContentService;

	element:ElementRef;
	$element:JQuery;

	blogPosts:BlogPost[];

	constructor( router:Router, element:ElementRef, dcl:DynamicComponentLoader, contentService:ContentService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.contentService = contentService;
	}

	getPostsList():void {
		console.log("");
		this.contentService.getPostsList().then(
			( blogPosts ) => {
				this.blogPosts = blogPosts;
			}
		).catch( console.error );
	}


	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	onActivate():void {
		this.getPostsList();
		//this.contentService.getPostsList().then(
		//		( blogPosts ) => {
		//			console.log( blogPosts );
		//			this.blogPosts = blogPosts;
		//			this.blogPosts.forEach( ( post:BlogPost, index:Number )=> {
		//				this.contentService.getPost( post.filename ).then(
		//						( content )=> {
		//							post.content = content;
		//							console.log( post );
		//							this.dcl.loadIntoLocation( BlogPostView, this.element, "post" );
		//						}
		//				);
		//			} );
		//		}
		//).catch( console.error );

		//@Component( {
		//	selector: 'compiled-component',
		//	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
		//	templateUrl: 'assets/blog-posts/' + turl
		//} )
		//class CompiledComponent {
		//}
		//this.dcl.loadIntoLocation( BlogPostSummaryView, this.element, 'content' );
	}
}