import { Component, CORE_DIRECTIVES } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, Location, RouteConfig, RouterLink, Router } from 'angular2/router';

import Carbon from 'carbonldp-sdk';

import HomeView from 'app/home/HomeView';
import BlogView from 'app/blog/BlogView';
import LoginView from 'app/login/LoginView';
import SPARQLClientComponent from 'app/sparql-client/SPARQLClientComponent';

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
	{path: '/', redirectTo: '/Home'},
	{path: '/Home', as: 'Home', component: HomeView},
	{path: '/Blog', as: 'Blog', component: BlogView},
	{path: '/Login', as: 'Login', component: LoginView},
	{path: '/sparql-client', as: 'SPARQLClient', component: SPARQLClientComponent}
] )
export default class App {

}