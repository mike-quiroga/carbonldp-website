import { Component } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from "angular2/router";

import Carbon from "carbon/Carbon";

import HomeView from "app/website/home/HomeView";

import LoginView from "app/website/auth/login/LoginView";

import BlogView from "app/website/blog/BlogView";
import BlogPostView from "app/website/blog/blog-post/BlogPostView";

import ContentView from "app/content/ContentView";

import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";
import UIExamplesView from "app/website/ui-examples/UIExamplesView";

import HeaderComponent from "app/website/header/HeaderComponent";
import FooterComponent from "app/website/footer/FooterComponent";

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

	{path: "docs/:id", as: "Docs", component: ContentView},

	{path: "sparql-client", as: "SPARQLClient", component: SPARQLClientComponent},
	{path: "ui-examples", as: "UIExamples", component: UIExamplesView},
] )
export default class WebsiteView {

}