import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { routing } from "./website.routing";

// Components
import { HeaderComponent } from "./header/header.component";
import { FooterComponent } from "./footer/footer.component";
import { RegisterComponent } from "./register/register.component";
import { SidebarComponent } from "./documentation/sidebar/sidebar.component";
import { NewsletterFormComponent } from "./newsletter-form/newsletter-form.component";
import { Angulartics2On } from "angulartics2/src/core/angulartics2On";
import { WebsiteView } from "./website.view";
import { HomeView } from "./home/home.view";
import { RegisterView } from "./register/register.view";
import { UIExamplesView } from "./ui-examples/ui-examples.view";
import { SignupThanksView } from "./signup-thanks/signup-thanks.view";
import { QuickStartGuideView } from "./documentation/quick-start-guide/quick-start-guide.view";
import { AboutCarbonLDPView } from "./about/about-carbon-ldp.view";
import { LicenseView } from "./license/license.view";


// Documentation
import { DocumentationView } from "./documentation/documentation.view";
import { HomeView as DocumentationHomeView } from "./documentation/home/home.view";

// Documentation - > Essential Concepts
import { EssentialConceptsView } from "./documentation/essential-concepts/essential-concepts.view";
import { LinkedDataConceptsView } from "./documentation/essential-concepts/linked-data-concepts.view";
// import { LDPConceptsView } from "./documentation/essential-concepts/ldp-concepts.view";
// import { CarbonLDPConceptsView } from "./documentation/essential-concepts/carbon-ldp-concepts.view";

// Documentation - > RESTApi
import { RESTApiView } from "./documentation/rest-api/rest-api.view";
import { GettingStartedView } from "./documentation/rest-api/getting-started.view";
import { InteractionModelsView } from "./documentation/rest-api/interaction-models.view";
import { ObjectModelView } from "./documentation/rest-api/object-model.view";
// import { RDFSourceView } from "./documentation/rest-api/rdf-source.view";
// import { ContainersView } from "./documentation/rest-api/containers.view";

// Documentation - > JavaScriptSDK
import { JavaScriptSDKView } from "./documentation/javascript-sdk/javascript-sdk.view";
import { GettingStartedView as JavaScriptSDKGettingStartedView } from "./documentation/javascript-sdk/getting-started.view";
import { ContextsView } from "./documentation/javascript-sdk/contexts.view";
import { ObjectModelView as JavaScriptSDKObjectModelView } from "./documentation/javascript-sdk/object-model.view";
import { ObjectSchemaView } from "./documentation/javascript-sdk/object-schema.view";

// Modules
import { SemanticModule } from "carbonldp-panel/semantic/semantic.module";
import { DirectivesModule } from "carbonldp-panel/directives.module";

@NgModule( {
	imports: [
		CommonModule,
		SemanticModule,
		FormsModule,
		DirectivesModule,
		routing
	],
	declarations: [
		Angulartics2On,

		WebsiteView,
		HomeView,
		SignupThanksView,
		RegisterView,
		UIExamplesView,
		LicenseView,
		AboutCarbonLDPView,

		HeaderComponent,
		FooterComponent,
		NewsletterFormComponent,
		SidebarComponent,
		RegisterComponent,

		// Documentation
		DocumentationView,
		DocumentationHomeView,
		QuickStartGuideView,

		// Documentation - > Essential Concepts
		EssentialConceptsView,
		LinkedDataConceptsView,

		// Documentation - > RESTApi
		RESTApiView,
		GettingStartedView,
		InteractionModelsView,
		ObjectModelView,

		// Documentation - > JavaScriptSDK
		JavaScriptSDKView,
		JavaScriptSDKGettingStartedView,
		ContextsView,
		JavaScriptSDKObjectModelView,
		ObjectSchemaView
	],
} )
export class WebsiteModule {
}