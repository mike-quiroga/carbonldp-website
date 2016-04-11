import {Component, ElementRef, Input} from "angular2/core";
import {CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators} from "angular2/common";
import {Router, ROUTER_DIRECTIVES} from "angular2/router";
import {Observable} from "rxjs";

import Carbon from "carbonldp/Carbon";
import * as Context from "carbonldp/Context";
import * as CarbonApp from "carbonldp/App";
import * as Apps from "carbonldp/Apps";
import * as HTTPResponse from "carbonldp/HTTP/Response";
import * as HTTPErrors from "carbonldp/HTTP/Errors";
import * as HTTPError from "carbonldp/HTTP/Errors/HTTPError";
import * as Pointer from "carbonldp/Pointer";

import $ from "jquery";
import "semantic-ui/semantic";

import AppContextService from "./../../../../AppContextService";
import App from "./../App";

import template from "./template.html!";

@Component( {
	selector: "edit-app",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, ],
} )
export default class EditAppComponent {
	carbon:Carbon;
	router:Router;
	element:ElementRef;

	$element:JQuery;
	$editAppForm:JQuery;
	appContextService:AppContextService;

	submitting:boolean = false;
	displaySuccessMessage:boolean = false;
	errorMessage:string = "";

	editAppForm:ControlGroup;
	formBuilder:FormBuilder;
	name:AbstractControl;
	description:AbstractControl;


	// Inputs and Outputs
	@Input() context:Context;


	constructor( router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
		this.appContextService = appContextService;
	}

	ngOnInit():void {
		console.log( this.context );
		// "https://carbonldp.com/ns/v1/security#AllOrigins"
		// this.context.app.allowsOrigin
		this.$element = $( this.element.nativeElement );
		this.$editAppForm = this.$element.find( "form.editAppForm" );
		this.editAppForm = this.formBuilder.group( {
			name: [ this.context.app.name, Validators.compose( [ Validators.required ] ) ],
			description: [ this.context.app.description, Validators.compose( [ Validators.required ] ) ],
		} );
		this.name = this.editAppForm.controls[ "name" ];
		this.description = this.editAppForm.controls[ "description" ];
	}

	ngAfterContentInit():void {

	}

	getSanitizedSlug( slug:string ):string {
		if ( slug ) {
			slug = slug.toLowerCase().replace( /[^\w ]+/g, "" ).replace( / +/g, "-" );
			if ( slug.charAt( slug.length - 1 ) !== "/" ) slug += "/";
		}
		return slug;
	}

	canDisplayErrors():boolean {
		return (! this.name.pristine && ! this.name.valid) || (! this.description.pristine && ! this.description.valid);
	}

	onSubmit( data:{ name:string, description:string }, $event:any ):void {
		$event.preventDefault();

		this.submitting = true;
		this.errorMessage = "";

		this.name.markAsDirty( true );
		this.description.markAsDirty( true );

		if ( ! this.editAppForm.valid ) {
			this.submitting = false;
			return;
		}

		let name:string = data.name;
		let description:string = data.description;

		if ( name ) this.context.app.name = name;
		if ( description ) this.context.app.description = description;

		this.context.app.save().then(
			():void => {
				this.displaySuccessMessage = true;
			},
			( error:HTTPError.HTTPError ):void => {
				this.setErrorMessage( error );
			}
		).then(
			():void => {
				this.submitting = false;
			}
		);
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
				this.errorMessage = "An error occurred while trying to update the app. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTPErrors.ServiceUnavailableError:
				this.errorMessage = "Service currently unavailable.";
				break;
			case error instanceof HTTPErrors.UnknownError:
				this.errorMessage = "An error occurred while trying to update the app. Please try again later. Error: " + error.response.status;
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
