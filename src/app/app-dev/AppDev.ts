import { Provider } from "angular2/core";
import AppDevComponent from "app/app-dev/AppDevComponent";
import { SIDEBAR_PROVIDERS } from "app/app-dev/components/sidebar/Sidebar";

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevComponent, {
		useClass: AppDevComponent
	} ),
	SIDEBAR_PROVIDERS
];