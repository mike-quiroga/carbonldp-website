import { Component, ElementRef, Input, Output, Inject, EventEmitter } from "@angular/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "@angular/common";

import { AuthService } from "angular2-carbonldp/services";

import Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";

import { EmailValidator } from "carbon-panel/custom-validators";
import { ValidatorFn } from "@angular/common/src/forms/directives/validators";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./registration.component.html!";

@Component( {
	selector: "cp-registration",
	template: template,
	styles: [ ":host { display:block; } " ],
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES ],
} )
export class RegistrationComponent {
	@Input( "container" ) container:string|JQuery;
	@Output( "onRegistration" ) onRegistration:EventEmitter<Credentials> = new EventEmitter<Credentials>();

	element:ElementRef;

	$element:JQuery;
	$registrationForm:JQuery;

	sending:boolean = false;
	errorMessage:string = "";

	registrationForm:ControlGroup;

	email:AbstractControl; // To make available the state of the input in the template
	matchingPassword:ControlGroup;
	passwordGroup:ControlGroup;
	password:AbstractControl; // To make available the state of the input in the template
	confirmPassword:AbstractControl;

	private formBuilder:FormBuilder; // Validators
	private authService:AuthService.Class;

	constructor( element:ElementRef, formBuilder:FormBuilder, @Inject( AuthService.Token ) authService:AuthService.Class ) {
		this.element = element;
		this.formBuilder = formBuilder;
		this.authService = authService;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$registrationForm = this.$element.find( "form.registrationForm" );
		this.$registrationForm.find( ".ui.checkbox" ).checkbox();
		this.registrationForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, EmailValidator ] ) ],
			matchingPassword: this.formBuilder.group({
				password: [ "", Validators.compose( [ Validators.required ] ) ],
				confirmPassword: [ "", Validators.compose( [ Validators.required ] ) ],
			}, { validator: this.matchPasswordValidator }),
		} );

		this.email = this.registrationForm.controls[ "email" ];
		this.passwordGroup = <ControlGroup>this.registrationForm.controls[ "matchingPassword"];
		this.password = this.passwordGroup.controls[ "password" ];
		this.confirmPassword = this.passwordGroup.controls[ "confirmPassword" ];
	}

	onSubmit( data:{ email:string, password:string, confirmPassword:string }, $event:any ):void {
		$event.preventDefault();
		//this.sending = true;
		this.errorMessage = "";
		this.email.markAsTouched();
		this.password.markAsTouched();
		this.confirmPassword.markAsTouched();
		if( ! this.registrationForm.valid ) {
			this.shakeForm();
			this.sending = false;
			return;
		}

		 //TODO: Add registration service

	}

	matchPasswordValidator( group:ControlGroup ):{ [key:string]:any } {
		let password = group.controls[ "password" ];
		let confirm = group.controls[ "confirmPassword" ];
		if( password.value === confirm.value ) {
			return null;
		} else {
			return { "passwordNotConfirmed": true };
		}
	}



	getDays( firstDate:Date, lastDate:Date ):number {
		// Discard the time and time-zone information
		let utc1:number = Date.UTC( firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate() );
		let utc2:number = Date.UTC( lastDate.getFullYear(), lastDate.getMonth(), lastDate.getDate() );
		let msPerDay:number = 1000 * 60 * 60 * 24;
		return Math.floor( (utc2 - utc1) / msPerDay );
	}

	setErrorMessage( error:HTTP.Errors.Error ):void {
		//TODO: Handle registration service errors
		/*switch ( true ) {
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
		}*/
	}

	shakeForm():void {
		let target:JQuery = this.container ? $( this.container ) : this.$element;
		if( ! target ) return;

		target.transition( {
			animation: "shake",
		} );
	}
}

export default RegistrationComponent;
