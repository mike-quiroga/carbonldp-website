import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";

import { RegisterComponent } from "./register.component";
import { NewsletterFormComponent } from "../newsletter-form/newsletter-form.component"

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./register.view.html!";
import style from "./register.view.css!text";

@Component( {
	selector: "register",
	template: template,
	directives: [ CORE_DIRECTIVES, RouterLink, RegisterComponent, NewsletterFormComponent ],
	styles: [ style ]
} )
export class RegisterView {
}

export default RegisterView;
