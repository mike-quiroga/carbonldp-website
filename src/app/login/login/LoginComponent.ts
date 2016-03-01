import { Component, ElementRef } from 'angular2/core';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouterLink } from 'angular2/router';
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, NgIf, Validators, AbstractControl } from "angular2/common";

import Carbon from "carbon/Carbon";
import * as Credentials from "carbon/Auth/Credentials";
import * as HTTP from "carbon/HTTP";
import Cookies from "js-cookie";
import AuthenticationToken from "carbon/Auth";

import { ValidationService } from "app/components/validation-service/ValidationService";

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';

@Component( {
	selector: 'login',
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, FORM_DIRECTIVES ]
} )
export default class LoginComponent {
	static parameters = [
		[ Router ], [ ElementRef ], [ FormBuilder ],
		[ Carbon ]
	];

	carbon:Carbon;
	router:Router;
	element:ElementRef;
	private cookiesHandler:Cookies;

	$element:JQuery;
	$loginForm:JQuery;

	submitted:boolean = false;
	sending:boolean = false;
	errorMessage:string = "";

	loginForm:ControlGroup;
	formBuilder:FormBuilder; // Validators
	email:AbstractControl; // To make available the state of the input in the template
	password:AbstractControl; // To make available the state of the input in the template


	constructor( public router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
		this.cookiesHandler = Cookies;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$loginForm = this.$element.find( 'form.loginForm' );
		this.loginForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ],
			password: [ "", Validators.compose( [ Validators.required ] ) ]
		} );
		this.email = this.loginForm.controls[ "email" ];
		this.password = this.loginForm.controls[ "password" ];
	}

	onSubmit( data:{ email:string, password:string }, $event:any ) {
		$event.preventDefault();

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
			( credentials:Credentials ) => {
				this.sending = false;
				// TODO: Change this to store a token when the SDK provides a way of authenticate using tokens.
				let token:AuthenticationToken = credentials.token;
				let days:number = this.getDays( (new Date()), token.expirationTime );
				let emailAndPassword = {
					email: this.email.value,
					password: this.password.value
				};
				this.cookiesHandler.set( "carbon_jwt", emailAndPassword, days );
				//this.router.navigate( [ "/AppDev" ] );
				this.router.navigate( [ '/AppDev' ] );
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
	}

	getDays( firstDate:Date, lastDate:Date ):number {
		// Discard the time and time-zone information
		let utc1 = Date.UTC( firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate() );
		let utc2 = Date.UTC( lastDate.getFullYear(), lastDate.getMonth(), lastDate.getDate() );
		let ms_per_day = 1000 * 60 * 60 * 24;
		return Math.floor( (utc2 - utc1) / ms_per_day );
	}

	shakeForm():void {
		let target:JQuery = $( ".login.popup" );
		target = target.length > 0 ? target : this.$element.find( ".formContainer" );
		if ( target ) {
			target.transition( {
				animation: "shake"
			} );
		}
	}

	login():void {

	}
}