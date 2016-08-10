import { Component, ElementRef, Input, DynamicComponentLoader, Type } from "@angular/core";
import { CORE_DIRECTIVES, Location } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import BlogService from "./../service/BlogService";
import BlogPost from "./BlogPost";
// import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";
import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "blog-post",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
	providers: [ BlogService, Location ]
} )
export default class BlogPostView {
	router: Router;
	dcl: DynamicComponentLoader;
	routeParams: RouteParams;
	location: Location;

	blogService: BlogService;

	element: ElementRef;
	$element: JQuery;

	blogPost: BlogPost = new BlogPost();
	postID: number;

	get codeMirrorMode() { return CodeMirrorComponent.Mode; }

	constructor( router: Router, element: ElementRef, dcl: DynamicComponentLoader, routeParams: RouteParams, location: Location, blogService: BlogService ) {
		this.router = router;
		this.element = element;
		this.dcl = dcl;
		this.routeParams = routeParams;
		this.location = location;

		this.blogService = blogService;

		// TODO: What if the postID is an invalid string?
		this.postID = parseInt( this.routeParams.get( "id" ) );
	}


	routerOnActivate(): void {
		if( typeof this.postID === "undefined" || this.postID === null ) {
			return;
		} else {
			this.blogService.getPost( this.postID ).then(
				( post )=> {
					this.blogPost = post;

					let fileName: string = post.filename;
					let postComponent: Type = this.createPostComponent( fileName );
					this.renderPostComponent( postComponent );
				}
			);
		}
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
	}

	private createPostComponent( fileName: string ): Type {
		// 'this' doesn't point to where it should inside of decorators
		let view = this;
		@Component( {
			selector: "compiled-component",
			directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ],
			templateUrl: `${ view.location.platformStrategy.getBaseHref() }assets/blog-posts/${ fileName }`
		} )
		class CompiledComponent {
			element: ElementRef;

			constructor( element: ElementRef ) {
				this.element = element;
			}

			ngAfterViewInit(): void {

			}
		}

		return CompiledComponent;
	}

	private renderPostComponent( postComponent: Type ): void {
		this.dcl.loadIntoLocation( postComponent, this.element, "content" );
	}
}