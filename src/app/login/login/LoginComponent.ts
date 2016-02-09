import { Component, ElementRef } from 'angular2/core';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouterLink } from 'angular2/router';
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, NgIf, Validators, AbstractControl } from "angular2/common";

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
	static parameters = [ [ Router ], [ ElementRef ], [ FormBuilder ] ];

	router:Router;
	element:ElementRef;
	$element:JQuery;
	$loginForm:JQuery;

	submitted:boolean = false;
	errorMessage:string = "";

	loginForm:ControlGroup;
	formBuilder:FormBuilder; // Validators
	email:AbstractControl; // To make available the state of the input in the template
	password:AbstractControl; // To make available the state of the input in the template


	constructor( public router:Router, element:ElementRef, formBuilder:FormBuilder ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$loginForm = this.$element.find( 'form.loginForm' );
		this.loginForm = this.formBuilder.group( {
			email: [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] ) ],
			password: [ "", Validators.compose( [ Validators.required, ValidationService.passwordValidator ] ) ]
		} );
		this.email = this.loginForm.controls[ "email" ];
		this.password = this.loginForm.controls[ "password" ];
	}

	onSubmit( data:any, $event:any ) {
		$event.preventDefault();
		this.$loginForm.find( "button.submit" ).addClass( "loading" );
		this.submitted = true;
		this.email.markAsTouched();
		this.password.markAsTouched();
		if ( this.loginForm.valid ) {
			this.shakeForm();
			this.errorMessage = "Service temporary unavailable";
			//console.log( this.loginForm );
			//console.log( data );
			this.router.navigate( [ '/Home' ] );
		} else {
			this.shakeForm();
		}
		this.$loginForm.find( "button.submit" ).removeClass( "loading" );
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
}