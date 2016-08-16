import { Component, OnInit } from "@angular/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "@angular/common";
import { Router } from "@angular/router-deprecated";

import { EmailValidator } from "carbon-panel/custom-validators";

import "semantic-ui/semantic";

import template from "./newsletter-form.component.html!";
import style from "./newsletter-form.component.css!text";

@Component( {
	selector: "newsletter-form",
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES ],
	styles: [ style ],
} )

export class NewsletterFormComponent implements OnInit {
	private router:Router;
	private subscribeForm:ControlGroup;
	private email:AbstractControl;
	private redirectPage;
	private errorPage;
	private location;

	constructor( router:Router, formBuilder:FormBuilder ) {
		this.router = router;
		this.subscribeForm = formBuilder.group( {
			"email": [ "", Validators.compose( [ Validators.required, EmailValidator ] ) ]
		} );
		this.email = this.subscribeForm.controls[ "email" ];
		this.location = location;
	}

	ngOnInit() {
		this.redirectPage = document.location.origin + "/site/signup-thanks/";
		this.errorPage = document.location.origin + "/error/";
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
