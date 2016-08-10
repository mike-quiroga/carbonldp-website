import { Component, ElementRef } from "@angular/core";
import { Router } from "@angular/router-deprecated";

import Credentials from "carbonldp/Auth/Credentials";

import { NotAuthenticated } from "angular2-carbonldp/decorators";
import { LoginComponent } from "carbon-panel/login.component";

import { FooterComponent } from "../../app-dev/footer/footer.component";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./app-dev-login.view.html!";
import style from "./app-dev-login.view.css!text";

@NotAuthenticated( {
	redirectTo: [ "/AppDev" ],
} )
@Component( {
	selector: "app-dev-login.big-stone1",
	template: template,
	styles: [ style ],
	directives: [ LoginComponent, FooterComponent, ],
} )
export class AppDevLoginView {
	element:ElementRef;
	$element:JQuery;
	router:Router;

	constructor( element:ElementRef, router:Router ) {
		this.element = element;
		this.$element = $( this.element.nativeElement );
		this.router = router;
	}

	saveCredentials( credentials:Credentials ):void {
		this.router.navigate( [ "/AppDev" ] );
	}

}

export default AppDevLoginView;