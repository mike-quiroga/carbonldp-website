/// <reference path="./../../../typings/typings.d.ts" />
import { Provider } from "angular2/core";
import AppDevComponent from "app/app-dev/AppDevComponent";
import AppContextService from "app/app-dev/AppContextService";

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevComponent, {
		useClass: AppDevComponent,
		dependencies: AppDevComponent.dependencies
	} ),
	new Provider( AppContextService, {
		useClass: AppContextService,
		dependencies: AppContextService.dependencies
	} )
];