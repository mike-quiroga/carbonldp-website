import { Component, CORE_DIRECTIVES, DynamicComponentLoader, ElementRef, Type } from "angular2/angular2";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";

import BlogService from "./service/BlogService";
import BlogPost from "./blog-post/BlogPost";
import BlogPostSummaryView from "./blog-post-summary/BlogPostSummaryView";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
//import template from './template.html!';


@Component( {
	selector: 'blog-test',
	template: `
		<div class="ui main grid container">
			<div class="row">
				<div class="twelve wide column">
					<blog-post-summary class="ui vertical segment" *ng-for="#post of blogPosts; #i=index;" [blog-post]="post"></blog-post-summary>
				</div>
				<aside class="four wide column">

				</aside>
			</div>
		</div>
	`,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, BlogPostSummaryView, CodeMirrorComponent.Class ],
	providers: [ BlogService ]
} )
export default class BlogTestView {
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


	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	onActivate():void {
		this.getPostsList();
		//this.blogService.getPostsList().then(
		//		( blogPosts ) => {
		//			console.log( blogPosts );
		//			this.blogPosts = blogPosts;
		//			this.blogPosts.forEach( ( post:BlogPost, index:Number )=> {
		//				this.blogService.getPost( post.filename ).then(
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