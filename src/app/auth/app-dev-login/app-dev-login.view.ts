import { Component, ElementRef } from "@angular/core";
import { Router } from "@angular/router";

import Credentials from "carbonldp/Auth/Credentials";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./app-dev-login.view.html!";
import style from "./app-dev-login.view.css!text";

@Component( {
	selector: "app-dev-login.big-stone1",
	template: template,
	styles: [ style ],
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
		this.router.navigate( [ "/app-dev" ] );
	}

}

export default AppDevLoginView;