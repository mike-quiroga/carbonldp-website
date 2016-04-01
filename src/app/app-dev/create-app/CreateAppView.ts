import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";
import { Observable } from "rxjs";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Apps from "carbonldp/Apps";
import * as HTTP from "carbonldp/HTTP";

import $ from "jquery";
import "semantic-ui/semantic";

import AppContextService from "./../AppContextService";

import template from "./template.html!";

@Component( {
	selector: "create-app",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class CreateAppView {
	carbon:Carbon;
	router:Router;
	element:ElementRef;

	$element:JQuery;
	$createAppForm:JQuery;

	submitting:boolean = false;
	displaySuccessMessage:boolean = false;
	errorMessage:string = "";

	_name:string = "";
	_slug:string = "";

	createAppForm:ControlGroup;
	formBuilder:FormBuilder;
	name:AbstractControl;
	slug:AbstractControl;
	description:AbstractControl;


	constructor( router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
	}

	ngOnInit():void {
		this.$element = $( this.element.nativeElement );
		this.$createAppForm = this.$element.find( "form.createAppForm" );
		this.createAppForm = this.formBuilder.group( {
			name: [ "", Validators.compose( [ Validators.required ] ) ],
			slug: [ "", Validators.compose( [ this.slugValidator ] ) ],
			description: [ "", Validators.compose( [ Validators.required ] ) ],
		} );
		this.name = this.createAppForm.controls[ "name" ];
		this.slug = this.createAppForm.controls[ "slug" ];
		this.description = this.createAppForm.controls[ "description" ];
	}

	ngAfterViewInit():void {
		this.name.valueChanges.subscribe(
			( value ):void => {
				if ( value ) {
					this._slug = this.getSanitizedSlug( value );
					this.slug.updateValueAndValidity( true, true );
				}
			}
		);
	}

	slugLostControl( $evt:FocusEvent ):void {
		if ( ! $evt.target.value.match( /^[a-z0-9]+(?:-[a-z0-9]*)*(?:\/*)$/ ) ) {
			(<Control> this.slug).updateValue( this.getSanitizedSlug( $evt.target.value ) );
			this._slug = this.slug.value;
		}
	}

	getSanitizedSlug( slug:string ):string {
		if ( slug ) {
			slug = slug.toLowerCase().replace( /[^\w ]+/g, "" ).replace( / +/g, "-" );
			if ( slug.charAt( slug.length - 1 ) !== "/" ) slug += "/";
		}
		return slug;
	}

	canDisplayErrors():boolean {
		return (! this.name.pristine && ! this.name.valid) || (! this.slug.pristine && ! this.slug.valid) || (! this.description.pristine && ! this.description.valid);
	}

	onSubmit( data:{ name:string, slug:string, description:string }, $event:any ):void {
		$event.preventDefault();

		this.submitting = true;
		this.errorMessage = "";

		this.name.markAsDirty( true );
		this.slug.markAsDirty( true );
		this.description.markAsDirty( true );

		if ( ! this.createAppForm.valid ) {
			this.submitting = false;
			return;
		}

		let name:string = data.name;
		let slug:string = data.slug;
		let description:string = data.description;

		let appFactory:App.Factory = App.Factory;
		let appDocument:App.Class = appFactory.create( name );
		appDocument.description = description;
		this.createApp( appDocument, slug ).then(
			( [appPointer, appCreationResponse]:[ Carbon.Pointer.Class, HTTP.Response.Class] ) => {
				this.submitting = false;
				this.displaySuccessMessage = true;
			},
			( error:HTTP.Errors.Error ) => {
				this.setErrorMessage( error );
				this.submitting = false;
			}
		);


	}

	createApp( appDocument:App.Class, slug?:string ):Promise<[ Carbon.Pointer.Class, HTTP.Response.Class]> {
		let promise:Promise<[ Carbon.Pointer.Class, HTTP.Response.Class]>;
		if ( ! ! slug ) {
			promise = this.carbon.apps.create( slug, appDocument );
		} else {
			promise = this.carbon.apps.create( appDocument );
		}
		return promise;
	}

	setErrorMessage( error:HTTP.Errors.Error ):void {
		switch ( true ) {
			case error instanceof HTTP.Errors.BadRequestError:
				this.errorMessage = "";
				break;
			case error instanceof HTTP.Errors.ConflictError:
				this.errorMessage = "There's already a resource with that slug. Error:" + error.response.status;
				break;
			case error instanceof HTTP.Errors.ForbiddenError:
				this.errorMessage = "Forbidden Action.";
				break;
			case error instanceof HTTP.Errors.NotFoundError:
				this.errorMessage = "Couldn't found the requested URL.";
				break;
			case error instanceof HTTP.Errors.RequestEntityTooLargeError:
				this.errorMessage = "Request entity too large.";
				break;
			case error instanceof HTTP.Errors.UnauthorizedError:
				this.errorMessage = "Unauthorized operation.";
				break;
			case error instanceof HTTP.Errors.InternalServerErrorError:
				this.errorMessage = "An error occurred while trying to create the app. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.ServiceUnavailableError:
				this.errorMessage = "Service currently unavailable.";
				break;
			case error instanceof HTTP.Errors.UnknownError:
				this.errorMessage = "An error occurred while trying to create the app. Please try again later. Error: " + error.response.status;
				break;
			default:
				this.errorMessage = "There was a problem processing the request. Error: " + error.response.status;
				break;
		}
	}


	slugValidator( slug:Control ):any {
		if ( slug.value ) {
			if ( slug.value.match( /^[a-z0-9]+(?:-[a-z0-9]*)*(?:\/*)$/ ) ) {
				return null;
			} else {
				return {"invalidSlug": true};
			}
		}
		return null;
	}

	closeMessage( evt:any ):void {
		let message:JQuery = $( evt.srcElement ).closest( ".ui.message" );
		message.transition( {
			onComplete: ():void => {
				if ( message.hasClass( "success" ) ) {
					this.displaySuccessMessage = false;
				} else {
					this.errorMessage = "";
				}
			},
		} ).transition( "fade" );
	}
}
