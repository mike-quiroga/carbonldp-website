import { Component } from "@angular/core";

import "semantic-ui/semantic";

import CreateAppComponent from "carbon-panel/my-apps/create-app/create-app.component";

import template from "./template.html!";

@Component( {
	selector: "create-app",
	template: template,
	directives: [ CreateAppComponent ],
} )
export default class CreateAppView {

	constructor() {
	}

}
