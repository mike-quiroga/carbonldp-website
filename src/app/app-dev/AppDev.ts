import { Provider } from "@angular/core";
import { AppDevView } from "app/app-dev/app-dev.view";
import AppContextService from "app/app-dev/AppContextService";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";
import DocumentsResolverService from "app/app-dev/my-apps/app/explorer/document-explorer/DocumentsResolverService";
import JobsService from "./my-apps/app/configuration/app-configuration/job/JobsService";
import BackupsService from "./my-apps/app/configuration/app-configuration/backup/BackupsService";
import { SIDEBAR_PROVIDERS } from "./components/sidebar/Sidebar";

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevView, {
		useClass: AppDevView
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
	new Provider( BackupsService, {
		useClass: BackupsService
	} ),
	SIDEBAR_PROVIDERS
];