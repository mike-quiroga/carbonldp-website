import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./register.view.html!";
import style from "./register.view.css!text";

@Component( {
	selector: "register-view",
	template: template,
	styles: [ style ]
} )
export class RegisterView {
}

export default RegisterView;
