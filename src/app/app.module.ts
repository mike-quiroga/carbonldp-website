import { NgModule } from "@angular/core";
import { APP_BASE_HREF } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { BrowserModule, Title } from "@angular/platform-browser";
import { HttpModule } from "@angular/http";

// Providers
import { URL_BASE } from "app/config";
import { CARBON_PROVIDERS } from "angular2-carbonldp/boot";
import { CARBON_SERVICES_PROVIDERS } from "angular2-carbonldp/services";
import { routing, appRoutingProviders } from "./app.routing";
import { Angulartics2 } from "angulartics2";
import { Angulartics2GoogleAnalytics } from "angulartics2/src/providers/angulartics2-google-analytics";

// Components
import { AppComponent } from "./app.component";
import { AppDevLoginView } from "./auth/app-dev-login/app-dev-login.view";
import { NotFoundErrorView } from "app/error-pages/not-found-error/not-found-error.view";

// Modules
import { PanelModule } from "carbonldp-panel/panel.module";


@NgModule( {
	imports: [
		BrowserModule,
		routing,
		HttpModule,
		FormsModule,
		PanelModule.forRoot()
	],
	declarations: [
		AppComponent,
		AppDevLoginView,
		NotFoundErrorView,
	],
	providers: [
		appRoutingProviders,
		{
			provide: APP_BASE_HREF,
			useValue: URL_BASE
		},
		Angulartics2,
		Angulartics2GoogleAnalytics,
		CARBON_PROVIDERS,
		CARBON_SERVICES_PROVIDERS,
		Title,
	],
	bootstrap: [ AppComponent ],
} )
export class AppModule {
}
