import { Provider } from "angular2/core";
import AppDevComponent from "app/app-dev/AppDevComponent";
import AppContextService from "app/app-dev/AppContextService";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";
import DocumentsResolverService from "app/app-dev/my-apps/app/explorer/document-explorer/DocumentsResolverService";
import JobsService from "app/app-dev/components/app-configuration/job/JobsService";
import { SIDEBAR_PROVIDERS } from "./components/sidebar/Sidebar";

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevComponent, {
		useClass: AppDevComponent
	} ),
	new Provider( AppContextService, {
		useClass: AppContextService
	} ),
	new Provider( ErrorsAreaService, {
		useClass: ErrorsAreaService
	} ),
	new Provider( DocumentsResolverService, {
		useClass: DocumentsResolverService
	} ),
	new Provider( JobsService, {
		useClass: JobsService
	} ),
	SIDEBAR_PROVIDERS
];