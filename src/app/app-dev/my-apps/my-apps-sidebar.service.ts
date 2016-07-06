import { Injectable, EventEmitter } from "@angular/core";

import { SidebarService, SidebarGroup, SidebarDivider, SidebarSubmenu } from "carbon-panel/sidebar.service";

import { App } from "./app/app";

@Injectable()
export class MyAppsSidebarService {

	private sidebarService:SidebarService;
	private openAppsGroup:SidebarGroup;
	private openApps:Map<App, SidebarSubmenu> = new Map<App, SidebarSubmenu>();
	private openAppsDivider:SidebarDivider = {
		type: "divider",
		name: "Open Apps",
		icon: "cubes icon",
		index: 0,
	};

	constructor( sidebarService:SidebarService ) {
		this.sidebarService = sidebarService;

		this.openAppsGroup = {
			type: "group",
			children: [],
			index: 100,
		};
		this.sidebarService.addItem( this.openAppsGroup );
	}

	addApp( app:App ):void {
		if( this.openApps.has( app ) ) return;

		if( this.openApps.size === 0 ) this.addOpenAppsDivider();

		let removeAppEmitter:EventEmitter<SidebarGroup> = new EventEmitter<SidebarGroup>();
		removeAppEmitter.subscribe( () => {
			this.removeApp( app );
		} );

		let appSubmenu:SidebarSubmenu = {
			type: "submenu",
			name: app.name,
			closeable: true,
			onClose: removeAppEmitter,
			children: [
				{
					type: "link",
					name: "Dashboard",
					icon: "bar chart icon",
					route: [ "AppDev", "MyApps", "App", { slug: app.slug }, "AppDashboard" ],
				},
				{
					type: "link",
					name: "Document Explorer",
					icon: "list layout icon",
					route: [ "AppDev", "MyApps", "App", { slug: app.slug }, "Explorer" ],
				},
				{
					type: "link",
					name: "SPARQL Editor",
					icon: "terminal icon",
					route: [ "AppDev", "MyApps", "App", { slug: app.slug }, "SPARQLEditor" ],
				},
				{
					type: "link",
					name: "Configuration",
					icon: "settings icon",
					route: [ "AppDev", "MyApps", "App", { slug: app.slug }, "Configuration" ],
				},
			]
		};
		this.openAppsGroup.children.push( appSubmenu );
		this.openApps.set( app, appSubmenu );
	}

	openApp( app:App ):void {
		if( ! this.openApps.has( app ) ) return;

		let appSubmenu:SidebarSubmenu = this.openApps.get( app );
		appSubmenu.open = true;
	}

	closeApp( app:App ):void {
		if( ! this.openApps.has( app ) ) return;

		let appSubmenu:SidebarSubmenu = this.openApps.get( app );
		appSubmenu.open = false;
	}

	removeApp( app:App ):void {
		if( ! this.openApps.has( app ) ) return;

		let appSubmenu:SidebarSubmenu = this.openApps.get( app );
		this.openAppsGroup.children.splice( this.openAppsGroup.children.indexOf( appSubmenu ), 1 );
		this.openApps.delete( app );

		if( this.openApps.size === 0 ) this.removeOpenAppsDivider();
	}

	private addOpenAppsDivider():void {
		this.openAppsGroup.children.push( this.openAppsDivider );
	}

	private removeOpenAppsDivider():void {
		this.openAppsGroup.children.splice( this.openAppsGroup.children.indexOf( this.openAppsDivider ), 1 );
	}
}

export default MyAppsSidebarService;
