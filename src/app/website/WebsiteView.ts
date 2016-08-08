import {Component, ElementRef} from "@angular/core";
import {CORE_DIRECTIVES, Location} from "@angular/common";
import {ROUTER_DIRECTIVES, RouteConfig, RouterLink, Router} from "@angular/router-deprecated";

import Carbon from "carbonldp/Carbon";

import HomeView from "app/website/home/HomeView";

import LoginView from "app/website/auth/login/LoginView";
import RegisterView from "app/website/register/register.view";

import BlogView from "app/website/blog/BlogView";
import BlogPostView from "app/website/blog/blog-post/BlogPostView";
import SignupThanksView from "./signup-thanks/SignupThanksView";

import UIExamplesView from "app/website/ui-examples/UIExamplesView";

import HeaderComponent from "app/website/header/HeaderComponent";
import FooterComponent from "app/website/footer/FooterComponent";
import DocumentationComponent from "app/website/documentation/DocumentationComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "website",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, HeaderComponent, FooterComponent ]
} )
@RouteConfig( [
	{ path: "", as: "Home", component: HomeView, useAsDefault: true },

	{
		path: "login", as: "Login", component: LoginView,
		data: {
			alias: "LogIn",
			displayName: "Log In"
		},
	},
	{
		path: "register", as: "Register", component: RegisterView,
		data: {
			alias: "Register",
			displayName: "Register"
		},
	},
	{
		path: "blog", as: "Blog", component: BlogView,
		data: {
			alias: "Blog",
			displayName: "Blog"
		},
	},
	{
		path: "blog/posts/:id", as: "BlogPost", component: BlogPostView,
		data: {
			alias: "Blog",
			displayName: "Blog"
		},
	},

	{ path: "documentation/...", as: "Documentation", component: DocumentationComponent },

	{
		path: "ui-examples", as: "UIExamples", component: UIExamplesView,
		data: {
			alias: "UI",
			displayName: "UI Examples"
		},
	},
	{
		path: "signup-thanks", as: "SignupThanks", component: SignupThanksView,
		data: {
			alias: "SignupThanks",
			displayName: "Thank you"
		},
	},
] )
export default class WebsiteView {
	element: ElementRef;
	$element: JQuery;
	router: Router;
	prevUrl = "";

	constructor( router: Router, element: ElementRef, private location: Location ) {
		this.element = element;
		this.router = router;
		this.router.parent.subscribe( ( url ) => {
			if( this.prevUrl !== url ) {
				$( "html, body" ).scrollTop( 0 );
				this.prevUrl = url;
			}
		} );
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
	}

}
