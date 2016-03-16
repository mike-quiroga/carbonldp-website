import {Injectable, Component, ElementRef, Injector } from "angular2/core";
import {RouteConfig, RouterOutlet, CanActivate, Router} from 'angular2/router';

import $ from "jquery";
import "semantic-ui/semantic";
import SidebarService from "./components/sidebar/service/SidebarService"
import Carbon from "carbon/Carbon";
import { CARBON_PROVIDER, appInjector } from "app/boot";
import AuthenticationToken from "carbon/Auth";
import * as Credentials from "carbon/Auth/Credentials";
import * as HTTP from "carbon/HTTP";
import Cookies from "js-cookie";

import SidebarComponent from "./components/sidebar/SidebarComponent";
import HeaderComponent from "./header/HeaderComponent";
import FooterComponent from "./footer/FooterComponent";
import MenuBarComponent from "./components/menubar/MenuBarComponent";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";

import DashboardView from "./dashboard/DashboardView";
import MyAppsView from "./my-apps/my-apps-view/MyAppsView";

import template from "./template.html!";
import "./style.css!";

@CanActivate(
	( prev, next )=> {
		let injector:Injector = appInjector();
		let carbon:Carbon = injector.get( Carbon );
		let router:Router = injector.get( Router );

		if ( ! carbon ) {
			router.navigate( [ "/Website/Login" ] );
			return false;
		}
		let cookiesHandler:Cookies = Cookies;
		let tokenCookie:Credentials = <Credentials>cookiesHandler.getJSON( "carbon_jwt" );
		if ( tokenCookie && ! carbon.auth.isAuthenticated() ) {
			return carbon.auth.authenticate( tokenCookie ).then(
				( credentials:Credentials ) => {
					return carbon.auth.isAuthenticated();
				}
			).catch(
				( error:Error ) => {
					switch ( true ) {
						case error instanceof HTTP.Errors.UnauthorizedError:
							console.log( "Wrong credentials" );
							break;
						default:
							console.log( "There was a problem processing the request" );
							break;
					}
					router.navigate( [ "/Website/Login" ] );
					return false;
				}
			);
		}
		if ( ! carbon.auth.isAuthenticated() )
			router.navigate( [ "/Website/Login" ] );
		return carbon.auth.isAuthenticated();
	}
)
@Component( {
	selector: "app-dev",
	template: template,
	directives: [ RouterOutlet, SidebarComponent, HeaderComponent, FooterComponent, MenuBarComponent ]
} )
@RouteConfig( [
	{
		path: "/",
		as: "Home",
		component: DashboardView,
		useAsDefault: true,
		data: {
			alias: "Home",
			displayName: "Home"
		}
	},
	{
		path: "/my-apps/...",
		as: "MyApps",
		component: MyAppsView,
		data: {
			alias: "MyApps",
			displayName: "My Apps"
		}
	}
] )
export default class AppDevComponent {
	element:ElementRef;
	$element:JQuery;

	sidebar:JQuery;
	sidebarService:SidebarService;

	constructor( element:ElementRef, sidebarService:SidebarService ) {
		this.element = element;
		this.sidebarService = sidebarService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.sidebar = this.$element.children( ".ui.sidebar" );
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}

