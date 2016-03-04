/// <reference path="./../../../../typings/typings.d.ts" />
import { Component, ElementRef, Injectable, Input } from "angular2/core";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router } from "angular2/router";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "angular2/common";

import { ValidationService } from "app/components/validation-service/ValidationService";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "login",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, FORM_DIRECTIVES ]
} )
@Injectable()
export default class LoginComponent {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	$loginForm:JQuery;

	submitting:boolean = false;
	errorMessage:string = "";

	loginForm:ControlGroup;
	formBuilder:FormBuilder; // Validators
	email:AbstractControl; // To make available the state of the input in the template
	password:AbstractControl; // To make available the state of the input in the template

	@Input() container:string|JQuery;

	constructor( public router:Router, element:ElementRef, formBuilder:FormBuilder ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$loginForm = this.$element.find( "form.loginForm" );
		this.loginForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ],
			password: [ "", Validators.compose( [ Validators.required, ValidationService.passwordValidator ] ) ]
		} );
		this.email = this.loginForm.controls[ "email" ];
		this.password = this.loginForm.controls[ "password" ];
	}

	onSubmit( data:any, $event:any ) {
		$event.preventDefault();
		this.submitting = true;
		this.email.markAsTouched();
		this.password.markAsTouched();
		if ( this.loginForm.valid ) {
			this.shakeForm();
			this.errorMessage = "Service temporary unavailable.";
			//this.router.navigate( [ "/AppDev/Home" ] );
		} else {
			this.shakeForm();
		}
		this.submitting = false;
	}

	shakeForm():void {
		let target:JQuery = this.$element.find( ".formContainer" );
		if ( this.container ) {
			target = $( this.container );
		}
		//target = target.length > 0 ? target : this.$element.find( ".formContainer" );
		if ( target ) {
			target.transition( {
				animation: "shake"
			} );
		}
	}
}