import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES, Location } from "@angular/common";
import { ROUTER_DIRECTIVES, RouteConfig, RouterLink, Router } from "@angular/router-deprecated";

import Carbon from "carbonldp/Carbon";

import HomeView from "app/website/home/HomeView";

import LoginView from "app/website/auth/login/LoginView";

import BlogView from "app/website/blog/BlogView";
import BlogPostView from "app/website/blog/blog-post/BlogPostView";
import SignupThanksView from "./signup-thanks/SignupThanksView";

import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
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

	{ path: "login", as: "Login", component: LoginView },

	{ path: "blog", as: "Blog", component: BlogView },
	{ path: "blog/posts/:id", as: "BlogPost", component: BlogPostView },

	{ path: "documentation/...", as: "Documentation", component: DocumentationComponent },

	{ path: "sparql-client", as: "SPARQLClient", component: SPARQLClientComponent },
	{ path: "ui-examples", as: "UIExamples", component: UIExamplesView },
	{ path: "signup-thanks", as: "SignupThanks", component: SignupThanksView },
] )
export default class WebsiteView {
	element:ElementRef;
	$element:JQuery;
	router:Router;
	prevUrl = "";

	constructor( router:Router, element:ElementRef, private location:Location ) {
		this.element = element;
		this.router = router;
		this.router.parent.subscribe( ( url ) => {
			if ( this.prevUrl !== url ) {
				$( "html, body" ).scrollTop( 0 );
				this.prevUrl = url;
			}
		} );
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

}
