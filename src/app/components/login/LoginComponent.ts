import { Component, ElementRef, Input, Inject } from "angular2/core";
import { ROUTER_DIRECTIVES, Router } from "angular2/router";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "angular2/common";

import { AuthService } from "angular2-carbonldp/services";

import Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";

import { ValidationService } from "app/components/validation-service/ValidationService";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "login",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, FORM_DIRECTIVES, ],
} )
export default class LoginComponent {
	router:Router;
	element:ElementRef;

	$element:JQuery;
	$loginForm:JQuery;

	submitting:boolean = false;
	sending:boolean = false;
	errorMessage:string = "";

	loginForm:ControlGroup;
	formBuilder:FormBuilder; // Validators
	email:AbstractControl; // To make available the state of the input in the template
	password:AbstractControl; // To make available the state of the input in the template
	rememberMe:AbstractControl;
	remember:boolean = true;

	@Input() container:string|JQuery;
	private authService:AuthService.Class;

	constructor( router:Router, element:ElementRef, formBuilder:FormBuilder, @Inject( AuthService.Token ) authService:AuthService.Class ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.authService = authService;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$loginForm = this.$element.find( "form.loginForm" );
		this.$loginForm.find( ".ui.checkbox" ).checkbox();
		this.loginForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ],
			password: [ "", Validators.compose( [ Validators.required ] ) ],
			rememberMe: [ "", Validators.compose( [] ) ],
		} );
		this.email = this.loginForm.controls[ "email" ];
		this.password = this.loginForm.controls[ "password" ];
		this.rememberMe = this.loginForm.controls[ "rememberMe" ];
	}

	onSubmit( data:{ email:string, password:string, rememberMe:boolean }, $event:any ):void {
		$event.preventDefault();
		this.submitting = true;
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
		let rememberMe:boolean = ! ! data.rememberMe;

		this.authService.login( username, password, rememberMe ).then( ( credential:Credentials ) => {
			this.sending = false;
			this.submitting = false;
			this.router.navigate( [ "/AppDev" ] );
		}).catch( ( error:HTTP.Errors.Error ) => {
			this.sending = false;
			this.setErrorMessage( error );
		});
		this.submitting = false;
	}

	getDays( firstDate:Date, lastDate:Date ):number {
		// Discard the time and time-zone information
		let utc1:number = Date.UTC( firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate() );
		let utc2:number = Date.UTC( lastDate.getFullYear(), lastDate.getMonth(), lastDate.getDate() );
		let msPerDay:number = 1000 * 60 * 60 * 24;
		return Math.floor( (utc2 - utc1) / msPerDay );
	}

	setErrorMessage( error:HTTP.Errors.Error ):void {
		switch ( true ) {
			case error instanceof HTTP.Errors.ForbiddenError:
				this.errorMessage = "Denied Access.";
				break;
			case error instanceof HTTP.Errors.UnauthorizedError:
				this.errorMessage = "Wrong credentials.";
				break;
			case error instanceof HTTP.Errors.BadGatewayError:
				this.errorMessage = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.GatewayTimeoutError:
				this.errorMessage = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.InternalServerErrorError:
				this.errorMessage = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.UnknownError:
				this.errorMessage = "An error occurred while trying to login. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.ServiceUnavailableError:
				this.errorMessage = "Service currently unavailable.";
				break;
			default:
				this.errorMessage = "There was a problem processing the request. Error: " + error.response.status;
				break;
		}
	}

	shakeForm():void {
		let target:JQuery = this.$element.find( ".formContainer" );
		if ( this.container ) {
			target = $( this.container );
		}
		if ( target ) {
			target.transition( {
				animation: "shake",
			} );
		}
	}
}
