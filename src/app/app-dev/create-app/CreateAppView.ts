import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "angular2/common";
import { Router, ROUTER_DIRECTIVES } from "angular2/router";
import { Observable } from "rxjs";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as Apps from "carbonldp/Apps";
import * as HTTPResponse from "carbonldp/HTTP/Response";
import * as HTTPErrors from "carbonldp/HTTP/Errors";
import * as HTTPError from "carbonldp/HTTP/Errors/HTTPError";
import * as Pointer from "carbonldp/Pointer";

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
	appContextService:AppContextService;

	submitting:boolean = false;
	displaySuccessMessage:boolean = false;
	errorMessage:string = "";

	_name:string = "";
	_slug:string = "";
	resolvedSlug:string = "";

	createAppForm:ControlGroup;
	formBuilder:FormBuilder;
	name:AbstractControl;
	slug:AbstractControl;
	description:AbstractControl;


	constructor( router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
		this.appContextService = appContextService;
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

	slugLostControl( evt:any ):void {
		if ( ! evt.target.value.match( /^[a-z0-9]+(?:-[a-z0-9]*)*(?:\/*)$/ ) ) {
			(<Control> this.slug).updateValue( this.getSanitizedSlug( evt.target.value ), false, false, false );
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

		let appDocument:App.Class = App.Factory.create( name );
		appDocument.description = description;
		this.carbon.apps.create( slug, appDocument ).then(
			( [appPointer, appCreationResponse]:[ Pointer.Class, HTTPResponse.Class] ) => {
				this.submitting = false;
				this.resolvedSlug = this._slug;
				this.carbon.apps.getContext( appPointer ).then(
					( appContext:App.Context ):void => {
						this.resolvedSlug = this.appContextService.getSlug( appContext );
					}
				);
				this.displaySuccessMessage = true;
			},
			( error:HTTPError.HTTPError ) => {
				this.setErrorMessage( error );
				this.submitting = false;
			}
		);


	}

	createApp( appDocument:App.Class, slug?:string ):Promise<[ Pointer.Class, HTTPResponse.Class]> {
		let promise:Promise<[ Pointer.Class, HTTPResponse.Class]>;
		if ( ! ! slug ) {
			promise = this.carbon.apps.create( slug, appDocument );
		} else {
			promise = this.carbon.apps.create( appDocument );
		}
		return promise;
	}

	setErrorMessage( error:HTTPError.HTTPError ):void {
		switch ( true ) {
			case error instanceof HTTPErrors.BadRequestError:
				this.errorMessage = "";
				break;
			case error instanceof HTTPErrors.ConflictError:
				this.errorMessage = "There's already a resource with that slug. Error:" + error.response.status;
				break;
			case error instanceof HTTPErrors.ForbiddenError:
				this.errorMessage = "Forbidden Action.";
				break;
			case error instanceof HTTPErrors.NotFoundError:
				this.errorMessage = "Couldn't found the requested URL.";
				break;
			case error instanceof HTTPErrors.RequestEntityTooLargeError:
				this.errorMessage = "Request entity too large.";
				break;
			case error instanceof HTTPErrors.UnauthorizedError:
				this.errorMessage = "Unauthorized operation.";
				break;
			case error instanceof HTTPErrors.InternalServerErrorError:
				this.errorMessage = "An error occurred while trying to create the app. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTPErrors.ServiceUnavailableError:
				this.errorMessage = "Service currently unavailable.";
				break;
			case error instanceof HTTPErrors.UnknownError:
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
