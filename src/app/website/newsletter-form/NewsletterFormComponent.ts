import { Component } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Location} from "angular2/router";

import { ValidationService } from "app/components/validation-service/ValidationService";

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
	redirectPage = document.location.href + "/signup-thanks/";
	errorPage = document.location.href;

	constructor( router:Router, formBuilder:FormBuilder ) {
		this.router = router;
		this.subscribeForm = formBuilder.group( {
			"email": [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ]
		} );
		this.email = this.subscribeForm.controls[ "email" ];
	}

	onSubmit( $event:any ):void {

		this.email.markAsTouched();
		let icpForm:HTMLElement = document.getElementById( 'icpsignup' );

		if ( this.subscribeForm.valid ) {
			icpForm.action = "https://app.icontact.com/icp/signup.php";
			icpForm.submit();
		}
	}
}
