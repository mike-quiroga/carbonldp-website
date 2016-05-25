import { Provider } from "@angular/core";
import SidebarService from "app/app-dev/components/sidebar/service/SidebarService";

export const SIDEBAR_PROVIDERS = [
	new Provider( SidebarService, {
		useFactory: () => {
			return new SidebarService();
		}
	} )
];