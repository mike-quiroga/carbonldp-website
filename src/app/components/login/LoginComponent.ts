import { Component, ElementRef, Injectable, Input } from "angular2/core";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouterLink } from "angular2/router";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, NgIf, Validators, AbstractControl } from "angular2/common";

import Carbon from "carbonldp/Carbon";
import * as Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";
import Cookies from "js-cookie";
import AuthenticationToken from "carbonldp/Auth";

import { ValidationService } from "app/components/validation-service/ValidationService";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "login",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, FORM_DIRECTIVES ]
} )
export default class LoginComponent {
	carbon:Carbon;
	router:Router;
	element:ElementRef;
	private cookiesHandler:Cookies;

	$element:JQuery;
	$loginForm:JQuery;

	submitting:boolean = false;
	sending:boolean = false;
	errorMessage:string = "";

	loginForm:ControlGroup;
	formBuilder:FormBuilder; // Validators
	email:AbstractControl; // To make available the state of the input in the template
	password:AbstractControl; // To make available the state of the input in the template

	@Input() container:string|JQuery;

	constructor( public router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
		this.cookiesHandler = Cookies;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$loginForm = this.$element.find( "form.loginForm" );
		this.loginForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ],
			password: [ "", Validators.compose( [ Validators.required ] ) ]
		} );
		this.email = this.loginForm.controls[ "email" ];
		this.password = this.loginForm.controls[ "password" ];
	}

	onSubmit( data:{ email:string, password:string }, $event:any ) {
		$event.preventDefault();
		this.submitting = true;
		this.submitted = true;
		this.sending = true;
		this.errorMessage = "";
		this.email.markAsTouched();
		this.password.markAsTouched();
		if ( ! this.loginForm.valid ) {
			this.shakeForm();
			this.sending = false;
			return;
		}

		let username:string = data.email;
		let password:string = data.password;

		this.carbon.auth.authenticate( username, password ).then(
			( credential:Credentials ) => {
				this.sending = false;
				this.submitting = false;
				let days:number = this.getDays( (new Date()), credential.expirationTime );
				this.cookiesHandler.set( "carbon_jwt", credential, days );
				this.router.navigate( [ "/AppDev" ] );
			} ).catch( ( error:Error ) => {
				this.sending = false;

				switch ( true ) {
					case error instanceof HTTP.Errors.UnauthorizedError:
						this.errorMessage = "Wrong credentials";
						break;
					default:
						this.errorMessage = "There was a problem processing the request";
						break;
				}
			}
		);
		this.submitting = false;
		this.errorMessage = "Service temporary unavailable.";
	}

	getDays( firstDate:Date, lastDate:Date ):number {
		// Discard the time and time-zone information
		let utc1 = Date.UTC( firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate() );
		let utc2 = Date.UTC( lastDate.getFullYear(), lastDate.getMonth(), lastDate.getDate() );
		let ms_per_day = 1000 * 60 * 60 * 24;
		return Math.floor( (utc2 - utc1) / ms_per_day );
	}

	shakeForm():void {
		let target:JQuery = this.$element.find( ".formContainer" );
		if ( this.container ) {
			target = $( this.container );
		}
		if ( target ) {
			target.transition( {
				animation: "shake"
			} );
		}
	}
}