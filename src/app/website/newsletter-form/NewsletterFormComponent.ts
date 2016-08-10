import { Component } from "@angular/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators, LocationStrategy } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router } from "@angular/router-deprecated";

import { EmailValidator } from "carbon-panel/custom-validators";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "newsletter-form",
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES ]
} )

export class NewsletterFormComponent {
	router:Router;
	subscribeForm:ControlGroup;
	email:AbstractControl;
	redirectPage;
	errorPage;
	location;

	constructor( router:Router, formBuilder:FormBuilder ) {
		this.router = router;
		this.subscribeForm = formBuilder.group( {
			"email": [ "", Validators.compose( [ Validators.required, EmailValidator ] ) ]
		} );
		this.email = this.subscribeForm.controls[ "email" ];
		this.location = location;
	}

	ngOnInit() {
		this.redirectPage = document.location.href + "/signup-thanks/";
		this.errorPage = document.location.href;
	}

	onSubmit( $event:any ):void {
		this.email.markAsTouched();
		let icpForm:HTMLElement = document.getElementById( 'icpsignup' );

		if( this.subscribeForm.valid ) {
			icpForm.action = "https://app.icontact.com/icp/signup.php";
			icpForm.submit();
		}
	}
}
