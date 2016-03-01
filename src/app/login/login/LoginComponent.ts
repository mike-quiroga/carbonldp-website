import { Component, ElementRef } from 'angular2/core';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouterLink } from 'angular2/router';
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, NgIf, Validators, AbstractControl } from "angular2/common";

import Carbon from "carbon/Carbon";
import * as Credentials from "carbon/Auth/Credentials";
import * as HTTP from "carbon/HTTP";

import { ValidationService } from "./../../components/validation-service/ValidationService";

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
		this.email.markAsTouched();
		this.password.markAsTouched();

		if( ! this.loginForm.valid ) {
			this.shakeForm();
			this.sending = false;
			return;
		}

		let username:string = data.email;
		let password:string = data.password;

		this.carbon.auth.authenticate( username, password ).then( ( credentials:Credentials ) => {
			this.sending = false;

			// TODO: Add remember me cookie

			this.router.navigate( [ '/AppDev' ] );
		}).catch( ( error:Error ) => {
			this.sending = false;

			switch( true ) {
				case error instanceof HTTP.Errors.UnauthorizedError:
					this.errorMessage = "Wrong credentials";
					break;
				default:
					this.errorMessage = "There was a problem processing the request";
					break;
			}
		});
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