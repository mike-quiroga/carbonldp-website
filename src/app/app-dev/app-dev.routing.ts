import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import { AuthenticatedGuard, NotAuthenticatedGuard } from "angular2-carbonldp/guards";
import { ActiveContextResolver } from "angular2-carbonldp/resolvers";

//Components
import { AppDevView } from "./app-dev.view";
import { NotFoundErrorView } from "./error-pages/not-found-error/not-found-error.view";
import { DashboardView } from "./dashboard/dashboard.view";

//Modules
import { MyAppsModule } from "carbonldp-panel/my-apps/my-apps.module";


const appDevRoutes:Routes = [
	{
		path: "",
		component: AppDevView,
		canActivate: [ AuthenticatedGuard ],
		data: {
			alias: "app-dev",
			onReject: [ "/login" ],
			onError: [ "/error" ],
			title: false,
		},
		children: [
			{
				path: "",
				component: DashboardView,
				data: {
					alias: "",
					displayName: "Dashboard",
					title: false,
				},
			},
			{
				path: "my-apps",
				loadChildren: () => MyAppsModule,
			},
			{
				path: "**",
				component: NotFoundErrorView,
				data: {
					title: "404 | Carbon LDP",
				}
			},
		]
	}
];

export const appDevRoutingProviders:any[] = [
	ActiveContextResolver,
	AuthenticatedGuard,
	NotAuthenticatedGuard,
];
export const routing:ModuleWithProviders = RouterModule.forChild( appDevRoutes );