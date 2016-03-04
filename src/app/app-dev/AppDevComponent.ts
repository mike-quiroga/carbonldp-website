/// <reference path="./../../../typings/typings.d.ts" />
import {Injectable, Component, ElementRef } from "angular2/core";
import {RouteConfig, RouterOutlet} from "angular2/router";

import $ from "jquery";
import "semantic-ui/semantic";
import SidebarService from "./components/sidebar/service/SidebarService"

import SidebarComponent from "./components/sidebar/SidebarComponent";
import HeaderComponent from "./header/HeaderComponent";
import FooterComponent from "./footer/FooterComponent";
import MenuBarComponent from "./components/menubar/MenuBarComponent";
import SPARQLClientComponent from "app/components/sparql-client/SPARQLClientComponent";

import DashboardView from "./dashboard/DashboardView";
import MyAppsView from "./my-apps/my-apps-view/MyAppsView";

import template from "./template.html!";
import "./style.css!";

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

