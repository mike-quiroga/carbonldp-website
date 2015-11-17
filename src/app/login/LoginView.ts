import { Component, View, CORE_DIRECTIVES, ElementRef} from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouterLink } from 'angular2/router';
import { FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, NgIf} from "angular2/angular2";
import { Validators} from 'angular2/angular2';

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import {AbstractControl} from "../../jspm_packages/npm/angular2@2.0.0-alpha.45/ts/src/core/forms/model";

@Component( {
	selector: 'login-page'
} )
@View( {
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, FORM_DIRECTIVES, NgIf, RouterLink ]
} )
export default class LoginView {
	static parameters = [ [ Router ], [ ElementRef ], [ FormBuilder ] ];

	public submitted:boolean = false;
	loginForm:ControlGroup;
	eMail:AbstractControl;
	passWord:AbstractControl;
	errorMessage:string;

	router:Router;
	element:ElementRef;
	$element:JQuery;
	$form:JQuery;

	constructor( public router:Router, element:ElementRef, fb:FormBuilder ) {
		this.router = router;
		this.element = element;

		this.loginForm = fb.group( {
			"eMail": [ "", Validators.compose( [ Validators.required, this.emailValidator ] ) ],
			"passWord": [ "", Validators.required ]
		} );
		this.eMail = this.loginForm.controls[ 'eMail' ];
		this.passWord = this.loginForm.controls[ 'passWord' ];
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$form = this.$element.find( '#loginForm' );


		this.loginForm.valueChanges.observer( {
			next: ( value ) => {
				if ( ((this.eMail.touched && this.eMail.hasError( 'required' )) || (this.passWord.touched && this.passWord.hasError( 'required' ))) ) {
					this.$element.find( '.ui.error.message' ).show();
				} else {
					this.$element.find( '.ui.error.message' ).hide();
				}
			}
		} );

	}

	emailValidator( control:Control ) {
		if ( ! control.value.match( /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/ ) ) {
			return {invalidEmail: true};
		}
	}

	onSubmit() {
		this.submitted = true;
		if ( ! this.loginForm.valid ) {
			this.$element.find( '.ui.error.message' ).show();
		} else {
			this.errorMessage = "Service temporary unavailable";
			console.log( "Valid Form:\n--- email:%o, \n--- password:%o", this.eMail.value, this.passWord.value );
			//this.router.navigate( [ '/Home' ] );
		}
	}
}