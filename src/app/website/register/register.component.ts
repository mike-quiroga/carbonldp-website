import { Component, ElementRef, Input, Output, Inject, EventEmitter, OnInit } from "@angular/core";

import { AuthService } from "angular2-carbonldp/services";
import Credentials from "carbonldp/Auth/Credentials";
import * as HTTP from "carbonldp/HTTP";


import $ from "jquery";
import "semantic-ui/semantic";

import template from "./register.component.html!";
import style from "./register.component.css!text";

@Component( {
	selector: "register",
	template: template,
	styles: [ style ],
} )

export class RegisterComponent implements OnInit {
	@Input( "container" ) container:string|JQuery;
	@Output( "onRegister" ) onRegister:EventEmitter<Credentials> = new EventEmitter<Credentials>();

	private element:ElementRef;
	private $element:JQuery;

	private $registrationForm:JQuery;

	register:{ email:string, password:string, confirmPassword:string } = {
		email: "",
		password: "",
		confirmPassword: ""
	}

	private authService:AuthService.Class;

	constructor( element:ElementRef, @Inject( AuthService.Token ) authService:AuthService.Class ) {
		this.element = element;
		this.authService = authService;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$registrationForm = this.$element.find( "form.registrationForm" );
		this.$registrationForm.find( ".ui.checkbox" ).checkbox();
	}

	onSubmit( data:{ email:string, password:string, confirmPassword:string }, $event:any ):void {
		$event.preventDefault();

		//TODO: Add registration service

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
