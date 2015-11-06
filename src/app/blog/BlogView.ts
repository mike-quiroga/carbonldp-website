import { Component, CORE_DIRECTIVES, ElementRef } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';

import BlogPostService from 'app/blog/posts/BlogPostService';
import BlogPost from 'app/blog/posts/BlogPost';

import template from './template.html!';

@Component({
	selector: 'blog',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
})
export default class BlogView {
	static parameters = [[ Router ], [ ElementRef ], [ BlogPostService ]];

	router:Router;

	blogPostService:BlogPostService;

	element:ElementRef;
	$element:JQuery;

	blogPosts:BlogPost[];

	constructor( router:Router, element: ElementRef, blogPostService:BlogPostService ){
		this.router = router;
		this.element = element;
		this.blogPostService = blogPostService;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	onActivate():void {
		this.blogPostService.getLatest().then( ( blogPosts ) => {
			this.blogPosts = blogPosts;
		}).catch( console.error );
	}
}