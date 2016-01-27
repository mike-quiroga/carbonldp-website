import {Injectable, Component, ElementRef } from 'angular2/angular2';
import {RouteConfig, RouterOutlet} from 'angular2/router';

//import $ from 'jquery';
//import 'semantic-ui/semantic';
import SidebarService from 'app/app-dev/components/sidebar/service/SidebarService'
import SidebarComponent from 'app/app-dev/components/sidebar/SidebarComponent';
import DashboardView from 'app/app-dev/dashboard/DashboardView';
import MyAppsView from 'app/app-dev/my-apps/MyAppsView';

import template from './template.html!';
import './style.css!';

@Component( {
	selector: 'app-dev',
	template: template,
	directives: [ RouterOutlet, SidebarComponent ]
} )
@RouteConfig( [
	{path: '/', as: 'Home', component: DashboardView},
	{path: '/my-apps', as: 'MyApps', component: MyAppsView}
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

	toggleSidebar():void {
		this.sidebarService.toggle();
	}
}

