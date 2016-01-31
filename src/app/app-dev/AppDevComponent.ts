import {Injectable, Component, ElementRef } from 'angular2/angular2';
import {RouteConfig, RouterOutlet} from 'angular2/router';

import $ from 'jquery';
import 'semantic-ui/semantic';
import SidebarService from './components/sidebar/service/SidebarService'

import SidebarComponent from './components/sidebar/SidebarComponent';
import HeaderComponent from './header/HeaderComponent ';
import FooterComponent from './footer/FooterComponent ';
import MenuBarComponent from './components/menubar/MenuBarComponent';

import DashboardView from './dashboard/DashboardView';
import MyAppsView from './my-apps/MyAppsView';

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'app-dev',
	template: template,
	directives: [ RouterOutlet, SidebarComponent, HeaderComponent, FooterComponent, MenuBarComponent ]
} )
@RouteConfig( [
	{path: '/', as: 'Home', displayName: 'Home', component: DashboardView, data: {alias: "Home", displayName: "Home"}},
	{path: '/my-apps', as: 'MyApps', displayName: 'My Apps', component: MyAppsView, data: {alias: "MyApps", displayName: "My Apps"}}
] )
@Injectable()
export default class AppDevComponent {
	static dependencies = AppDevComponent.parameters;
	static parameters = [ [ ElementRef ], [ SidebarService ] ];


	element:ElementRef;
	$element:JQuery;

	sidebar:JQuery;
	sidebarService:SidebarService;


	constructor( element:ElementRef, sidebarService:SidebarService ) {
		this.element = element;
		this.sidebarService = sidebarService;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		$( "app > header, app > footer" ).hide();
		this.sidebar = this.$element.children( ".ui.sidebar" );
	}

	onDestroy():void {
		$( "app > header, app > footer" ).show();
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}

