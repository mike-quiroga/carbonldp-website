import { Component, provide, Inject, OnInit, EventEmitter } from "@angular/core";

import { Router, NavigationEnd } from "@angular/router";

import { AuthService } from "angular2-carbonldp/services";

import { HeaderService } from "carbonldp-panel/header.service";
import { SidebarService } from "carbonldp-panel/sidebar.service";

import "semantic-ui/semantic";

import template from "./app-dev.view.html!";
import style from "./app-dev.view.css!text";

@Component( {
	selector: "app-dev",
	template: template,
	styles: [ style ],
})
export class AppDevView implements OnInit {

	private headerService:HeaderService;
	private sidebarService:SidebarService;
	private authService:AuthService.Class;
	private router:Router;
	private prevUrl:string;

	constructor( headerService:HeaderService, sidebarService:SidebarService, @Inject( AuthService.Token ) authService:AuthService.Class, router:Router ) {
		this.headerService = headerService;
		this.sidebarService = sidebarService;
		this.authService = authService;
		this.router = router;
		this.router.events.subscribe( ( event:Event )=> {
			let url:string = "", scrollableContent:Element;
			if( event instanceof NavigationEnd ) {
				url = event.url;
				if( this.prevUrl !== url ) {
					scrollableContent = document.querySelector( ".scrollable-content" );
					if( scrollableContent )scrollableContent.scrollTop = 0;
					this.prevUrl = url;
				}
			}
		} );
	}

	ngOnInit():void {
		this.populateHeader();
		this.populateSidebar();
	}

	toggleSidebar():void {
		this.sidebarService.toggle();
	}

	private populateHeader():void {
		this.headerService.clear();
		this.headerService.logo = {
			image: "assets/images/carbon-ldp-logo-lg.png",
			route: [ "" ]
		};

		let onLogout:EventEmitter<any> = new EventEmitter<any>();
		onLogout.subscribe( ( event:any ) => {
			this.authService.logout();
			this.router.navigate( [ "/login" ] );
		} );

		this.headerService.addItems( [
			{
				name: "Dashboard",
				route: [ "../app-dev" ],
				index: 0,
			},
			{
				name: "User",
				children: [
					{
						icon: "sign out icon",
						name: "Log Out",
						onClick: onLogout,
						index: 100,
					}
				],
				index: 100,
			}
		] );
	}

	private populateSidebar():void {
		this.sidebarService.addItems( [
			{
				type: "link",
				name: "Dashboard",
				route: [ "../app-dev" ],
				index: 0,
			},
			{
				type: "link",
				name: "Apps",
				route: [ "my-apps" ]
			}
		] );
	}
}

export default AppDevView;
