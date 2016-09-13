import { Component, ElementRef, Input, Output, Inject, EventEmitter, OnInit } from "@angular/core";

import { FormBuilder, ControlGroup, AbstractControl, Validators } from "@angular/common";
import { AuthService } from "angular2-carbonldp/services";

import Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";

import { EmailValidator } from "carbonldp-panel/custom-validators";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./register.component.html!";
import style from "./register.component.css!text";

@Component( {
	selector: "cp-register",
	template: template,
	styles: [ style ],
} )
export class RegisterComponent implements OnInit {
	@Input( "container" ) container:string|JQuery;
	@Output( "onRegister" ) onRegister:EventEmitter<Credentials> = new EventEmitter<Credentials>();

	private element:ElementRef;
	private $element:JQuery;

	private $registrationForm:JQuery;

	private sending:boolean = false;
	private errorMessage:string = "";

	private registrationForm:ControlGroup;
	private email:AbstractControl; // To make available the state of the input in the template
	private passwordGroup:ControlGroup;
	private password:AbstractControl; // To make available the state of the input in the template
	private confirmPassword:AbstractControl;

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
			passwordGroup: this.formBuilder.group( {
				password: [ "", Validators.compose( [ Validators.required ] ) ],
				confirmPassword: [ "", Validators.compose( [ Validators.required ] ) ],
			}, { validator: this.matchPasswordValidator } ),
		} );

		this.email = this.registrationForm.controls[ "email" ];
		this.passwordGroup = <ControlGroup>this.registrationForm.controls[ "passwordGroup" ];
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

	}

	shakeForm():void {
		let target:JQuery = this.container ? $( this.container ) : this.$element;
		if( ! target ) return;

		target.transition( {
			animation: "shake",
		} );
	}
}

export default RegisterComponent;
