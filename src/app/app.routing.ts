import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

//Guards
import { AuthenticatedGuard, NotAuthenticatedGuard } from "angular2-carbonldp/guards";
import { ActiveContextResolver } from "angular2-carbonldp/resolvers";

//Components
import { AppDevLoginView } from "app/auth/app-dev-login/app-dev-login.view";
import { NotFoundErrorView } from "app/error-pages/not-found-error/not-found-error.view";

import { WebsiteModule } from "./website/website.module";
import { AppDevModule } from "./app-dev/app-dev.module";

const appRoutes:Routes = [
	{
		path: "app-dev",
		data: {
			alias: "",
			title: "Carbon LDP",

		},
		loadChildren: () => AppDevModule,
	},
	{
		path: "login",
		component: AppDevLoginView,
		data: {
			alias: "AppDevLogIn",
			title: "Log In | Carbon LDP",
		},
	},
	{
		path: "",
		data: {
			title: "Carbon LDP"
		},
		loadChildren: () => WebsiteModule,
	},
	{
		path: "**",
		component: NotFoundErrorView,
		data: {
			title: "404 | Carbon LDP"
		},
	},
];


export const appRoutingProviders:any[] = [
	ActiveContextResolver,
	AuthenticatedGuard,
	NotAuthenticatedGuard,
];

export const routing:ModuleWithProviders = RouterModule.forRoot( appRoutes );