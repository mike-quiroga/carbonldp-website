import { Component } from "@angular/core";

import "semantic-ui/semantic";

import { RegisterFormComponent } from "./register-form.component";

import template from "./get-started.view.html!";

@Component( {
	template: template,
	directives: [
		RegisterFormComponent
	],
} )

export class GetStartedView {

}

export default GetStartedView;