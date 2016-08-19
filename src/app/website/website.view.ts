import { Component, ViewEncapsulation } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, Router } from "@angular/router-deprecated";


import HomeView from "./home/home.view";

import LoginView from "./auth/login/login.view";
import RegisterView from "app/website/register/register.view";

import BlogView from "./blog/blog.view";
import BlogPostView from "./blog/blog-post/blog-post.view";
import SignupThanksView from "./signup-thanks/signup-thanks.view";

import UIExamplesView from "./ui-examples/ui-examples.view";

import HeaderComponent from "./header/header.component";
import FooterComponent from "./footer/footer.component";
import DocumentationView from "app/website/documentation/documentation.view";

import template from "./website.view.html!";
import style from "./website.view.css!text";

@Component( {
	selector: "website",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, HeaderComponent, FooterComponent ],
	encapsulation: ViewEncapsulation.None,
	styles: [ style ],
} )
@RouteConfig( [
	{
		path: "", as: "Home", component: HomeView, useAsDefault: true,
		data: {
			description: {
				name: "description",
				content: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web.",
			},
		},
	},
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
			displayName: "Register",
		},
	},
	{
		path: "blog", as: "Blog", component: BlogView,
		data: {
			alias: "Blog",
			displayName: "Blog",
		},
	},
	{
		path: "blog/posts/:id", as: "BlogPost", component: BlogPostView,
		data: {
			alias: "Blog",
			displayName: "Blog",
		},
	},

	{ path: "documentation/...", as: "Documentation", component: DocumentationView },

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
			displayName: "Thank you",
			description: {
				name: "description",
				content: "Carbon's newsletter keeps you updated. Thank you, for signing up!",
			}
		},
	},
] )
export class WebsiteView {
	private router:Router;
	private prevUrl = "";

	constructor( router:Router ) {
		this.router = router;
		this.router.parent.subscribe( ( url ) => {
			if( this.prevUrl !== url ) {
				$( "html, body" ).scrollTop( 0 );
				this.prevUrl = url;
			}
		} );
	}

}

export default WebsiteView;