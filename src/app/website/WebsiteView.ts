import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from "angular2/router";

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
import DocumentsComponent from "app/website/documents/DocumentsComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "website",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, HeaderComponent, FooterComponent ]
} )
@RouteConfig( [
	{path: "", as: "Home", component: HomeView, useAsDefault: true},

	{path: "login", as: "Login", component: LoginView},

	{path: "blog", as: "Blog", component: BlogView},
	{path: "blog/posts/:id", as: "BlogPost", component: BlogPostView},

	{path: "documents/...", as: "Documents", component: DocumentsComponent},

	{path: "sparql-client", as: "SPARQLClient", component: SPARQLClientComponent},
	{path: "ui-examples", as: "UIExamples", component: UIExamplesView},
	{path: "signup-thanks", as: "SignupThanks", component: SignupThanksView},
] )
export default class WebsiteView {
	element:ElementRef;
	$element:JQuery;
	top:boolean = true;

	constructor( router:Router, element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.headerAnimation();
	}

	headerAnimation():void {
		let home = this.$element.find("#main-carbon-logo");
		home.visibility({
			once: false,
			offset: 80,
			onTopPassed: function():void {
				this.top = false;
			},
			onTopPassedReverse: function():void {
				this.top = true;
			}
		} );
	}

}
