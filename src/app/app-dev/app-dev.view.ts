import { Component, provide, Inject, OnInit, EventEmitter } from "@angular/core";
import { Location } from "@angular/common";
import { Router, RouteConfig, RouterOutlet } from "@angular/router-deprecated";

import { Authenticated } from "angular2-carbonldp/decorators";
import { AuthService } from "angular2-carbonldp/services";

import { RouterService } from "carbon-panel/router.service";
import { HeaderComponent } from "carbon-panel/header.component";
import { HeaderService } from "carbon-panel/header.service";
import { SidebarComponent } from "carbon-panel/sidebar.component";
import { SidebarService } from "carbon-panel/sidebar.service";
import { MenuBarComponent } from "carbon-panel/menu-bar.component";
import { ErrorsAreaComponent } from "carbon-panel/errors-area/errors-area.component";
import { ErrorsAreaService } from "carbon-panel/errors-area/errors-area.service";
import { MyAppsSidebarService } from "carbon-panel/my-apps/my-apps-sidebar.service";
import { MyAppsView } from "carbon-panel/my-apps/my-apps.view";

import { FooterComponent } from "./footer/footer.component";
import { DashboardView } from "./dashboard/dashboard.view";

import { NotFoundErrorView } from "./error-pages/not-found-error/not-found-error.view";

import "semantic-ui/semantic";

import template from "./app-dev.view.html!";
import style from "./app-dev.view.css!text";

@Authenticated( {
	redirectTo: [ "/AppDevLogin" ],
} )
@Component( {
	selector: "app-dev",
	template: template,
	styles: [ style ],
	directives: [
		RouterOutlet,
		SidebarComponent,
		HeaderComponent,
		FooterComponent,
		MenuBarComponent,
		ErrorsAreaComponent,
	],
	providers: [
		provide( RouterService, {
			useFactory: ( router:Router, location:Location ):RouterService => {
				return new RouterService( router, location );
			},
			deps: [ Router, Location ]
		} ),
		provide( HeaderService, { useClass: HeaderService } ),
		provide( SidebarService, { useClass: SidebarService } ),
		provide( ErrorsAreaService, { useClass: ErrorsAreaService } ),

		// If we provide MyAppsSidebarService inside of my-apps.view, Angular would create a new instance each time my-apps is revisited
		// leading to duplicate entries in the sidebar
		provide( MyAppsSidebarService, { useClass: MyAppsSidebarService } ),
	]
} )
@RouteConfig( [
	{
		path: "/",
		as: "Home",
		component: DashboardView,
		useAsDefault: true,
		data: {
			alias: "Home",
			displayName: "Home",
		},
	},
	{
		path: "/my-apps/...",
		as: "MyApps",
		component: MyAppsView,
		data: {
			alias: "MyApps",
			displayName: "My Apps",
		},
	},
	{
		path: "**",
		as: "NotFoundError",
		component: NotFoundErrorView,
		data: {
			alias: "NotFoundError",
			displayName: "404",
		},
	},
] )
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
		this.router.parent.subscribe( ( url )=> {
			if( this.prevUrl !== url ) {
				document.querySelector( ".scrollable-content" ).scrollTop = 0;
				this.prevUrl = url;
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
		this.headerService.logo = {
			image: "assets/images/carbon-ldp-logo-lg.png",
			route: [ "./Home" ]
		};

		let onLogout:EventEmitter<any> = new EventEmitter<any>();
		onLogout.subscribe( ( event:any ) => {
			this.authService.logout();
			this.router.navigate( [ "/AppDevLogin" ] );
		} );

		this.headerService.addItems( [
			{
				name: "Dashboard",
				route: [ "./Home" ],
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
				route: [ "./Home" ],
				index: 0,
			},
			{
				type: "link",
				name: "Apps",
				route: [ "AppDev", "MyApps" ]
			}
		] );
	}
}

export default AppDevView;
