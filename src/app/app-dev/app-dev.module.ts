import { NgModule } from "@angular/core";
import { CommonModule, DeprecatedFormsModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { routing, appDevRoutingProviders } from "./app-dev.routing";

// Providers
import { CARBON_PROVIDERS } from "angular2-carbonldp/boot";
import { CARBON_SERVICES_PROVIDERS } from "angular2-carbonldp/services";

// Components
import { FooterComponent } from "./footer/footer.component";
import { AppDevView } from "./app-dev.view";
import { NotFoundErrorView } from "./error-pages/not-found-error/not-found-error.view";
import { DashboardView } from "./dashboard/dashboard.view";

// Modules
import { PanelModule } from "carbonldp-panel/panel.module";


@NgModule( {
	imports: [
		CommonModule,
		// DeprecatedFormsModule,
		PanelModule,
		FormsModule,
		routing
	],
	declarations: [
		FooterComponent,
		AppDevView,
		NotFoundErrorView,
		DashboardView,

	],
	providers: [
		CARBON_PROVIDERS,
		CARBON_SERVICES_PROVIDERS,
		appDevRoutingProviders,
	],
} )
export class AppDevModule {
}