import { Component } from "angular2/core";
import { CORE_DIRECTIVES } from "angular2/common";
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from 'angular2/router';

import Carbon from 'carbon/Carbon';

import HomeView from 'app/home/HomeView';
import BlogView from 'app/blog/BlogView';
import BlogPostView from 'app/blog/blog-post/BlogPostView';
import LoginView from 'app/login/LoginView';
//import ContentView from 'app/content/ContentView';
import SPARQLClientComponent from 'app/sparql-client/SPARQLClientComponent';
import CarbonUI from 'app/carbon-ui/CarbonUI';
import DocumentsComponent from 'app/documents/DocumentsComponent';

import HeaderComponent from 'app/header/HeaderComponent';
import FooterComponent from 'app/footer/FooterComponent';
import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'app',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, HeaderComponent, FooterComponent ]
} )
@RouteConfig( [
	{path: '/', redirectTo: [ '/Home' ]},
	{path: '/Home', as: 'Home', component: HomeView},
	{path: '/Blog', as: 'Blog', component: BlogView},
	{path: '/blog-post/:id', as: 'BlogPost', component: BlogPostView},
	{path: '/Login', as: 'Login', component: LoginView},
	{path: '/sparql-client', as: 'SPARQLClient', component: SPARQLClientComponent},
	{path: '/carbon-ui', as: 'CarbonUI', component: CarbonUI},
	{path: '/documents/...', as: 'Documents', component: DocumentsComponent}
] )
export default class App {

}