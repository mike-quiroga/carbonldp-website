import { Component } from "@angular/core";

import { LoginComponent } from "carbon-panel/login.component";

import "semantic-ui/semantic";

import template from "./login.view.html!";

@Component( {
	selector: "login-page",
	template: template,
	directives: [ LoginComponent, ],
} )
export default class LoginView {
}
