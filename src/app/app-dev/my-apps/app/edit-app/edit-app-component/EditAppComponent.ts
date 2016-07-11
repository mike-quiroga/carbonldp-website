import { Component, ElementRef, Input } from "@angular/core";
import { CORE_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "@angular/common";
import { Router, ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import Carbon from "carbonldp/Carbon";
import * as Context from "carbonldp/App/Context";
import * as HTTP from "carbonldp/HTTP";
import * as PersistedApp from "carbonldp/PersistedApp";

import $ from "jquery";
import "semantic-ui/semantic";

// import AppContextService from "./../../../../AppContextService";
import { AppContextService } from "carbon-panel/my-apps/app-context.service";

import template from "./template.html!";
import "./style.css!";

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
	corsGroup:ControlGroup;
	formBuilder:FormBuilder;
	name:AbstractControl;
	description:AbstractControl;
	allDomains:AbstractControl;
	domain:AbstractControl;

	allowedDomains:string[] = [];
	domainStr:string = "";

	// Inputs and Outputs
	@Input() context:Context.Class;


	constructor( router:Router, element:ElementRef, formBuilder:FormBuilder, carbon:Carbon, appContextService:AppContextService ) {
		this.router = router;
		this.element = element;
		this.formBuilder = formBuilder;
		this.carbon = carbon;
		this.appContextService = appContextService;
	}

	ngOnInit():void {
		let allowAllOrigins:boolean = false;
		if( ! ! this.context.app.allowsOrigins && this.context.app.allowsOrigins.length > 0 ) {
			allowAllOrigins = this.context.app.allowsOrigins[ 0 ][ "id" ] === Carbon.NS.CS.Class.AllOrigins;
			if( ! allowAllOrigins )this.allowedDomains = <string[]>this.context.app.allowsOrigins;
		}
		this.$element = $( this.element.nativeElement );

		this.$editAppForm = this.$element.find( "form.editAppForm" );
		this.editAppForm = this.formBuilder.group( {
			name: [ this.context.app.name, Validators.compose( [ Validators.required ] ) ],
			description: [ this.context.app.description, Validators.compose( [ Validators.required ] ) ],
			cors: this.formBuilder.group( {
				allDomains: [ allowAllOrigins ],
				domain: [ this.domainStr ],
				allowedDomains: [ this.allowedDomains ],
			}, { validator: Validators.compose( [ this.domainValidator, this.allowedDomainsValidator ] ) } ),
		} );
		this.name = this.editAppForm.controls[ "name" ];
		this.description = this.editAppForm.controls[ "description" ];
		this.corsGroup = <ControlGroup>this.editAppForm.controls[ "cors" ];
		this.allDomains = this.corsGroup.controls[ "allDomains" ];
		this.domain = this.corsGroup.controls[ "domain" ];
	}

	domainValidator( corsGroup:ControlGroup ):any {
		let allDomains:AbstractControl = corsGroup.controls[ "allDomains" ];
		let domain:AbstractControl = corsGroup.controls[ "domain" ];
		if( allDomains.value || (! allDomains.value && ! ! domain.value && ! ! domain.value.match( /^http(s?):\/\/((\w+\.)?\w+\.\w+|((2[0-5]{2}|1[0-9]{2}|[0-9]{1,2})\.){3}(2[0-5]{2}|1[0-9]{2}|[0-9]{1,2}))(\/)?$/gm ) ) ) {
			return null;
		}
		if( ! ! domain.value ) {
			return { "invalidURLAddress": true };
		}
	}

	allowedDomainsValidator( corsGroup:ControlGroup ):any {
		if( ! corsGroup.value[ "allDomains" ] && (<string[]>corsGroup.value[ "allowedDomains" ]).length <= 0 ) {
			return { "emptyAllowedAddresses": true };
		}
		return null;
	}

	addDomain( domain:string ):void {
		if( this.domain.valid && ! ! domain ) this.allowedDomains.push( domain );
		this.corsGroup.updateValueAndValidity();
	}

	removeDomain( option:string ):void {
		let idx:number = this.allowedDomains.indexOf( option );
		if( idx >= 0 ) {
			this.allowedDomains.splice( idx, 1 );
			this.corsGroup.updateValueAndValidity();
		}
	}

	canDisplayErrors():boolean {
		return (! this.name.pristine && ! this.name.valid) || (! this.description.pristine && ! this.description.valid);
	}

	onSubmit( data:{ name:string, description:string, cors:{allDomains:boolean, domain:string, allowedDomains:string[], } }, $event:Event ):void {
		$event.preventDefault();

		this.submitting = true;
		this.errorMessage = "";

		this.name.markAsDirty( true );
		this.description.markAsDirty( true );

		if( ! this.editAppForm.valid ) {
			this.submitting = false;
			return;
		}

		let name:string = data.name;
		let description:string = data.description;
		let allowsAllOrigin:any = data.cors.allDomains;
		let allowedDomains:string[] = data.cors.allowedDomains;

		if( name ) this.context.app.name = name;
		if( description ) this.context.app.description = description;
		if( allowsAllOrigin ) {
			this.context.app.allowsOrigins = [ Carbon.Pointer.Factory.create( Carbon.NS.CS.Class.AllOrigins ) ];
		} else {
			this.context.app.allowsOrigins = allowedDomains.length > 0 ? allowedDomains : this.context.app.allowsOrigins;
		}

		this.context.app.save().then( ( [updatedApp, response]:[PersistedApp.Class, HTTP.Response.Class] ):void => {
			this.context.app.refresh().catch( ( error:HTTP.Errors.Error ):void => this.setErrorMessage( error ) );
			this.displaySuccessMessage = true;
		} ).catch( ( error:HTTP.Errors.Error ):void => {
			this.setErrorMessage( error );
		} ).then( ():void => {
			this.submitting = false;
		} );
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
				this.errorMessage = "An internal error occurred while trying to update the app. Please try again later. Error: " + error.response.status;
				break;
			case error instanceof HTTP.Errors.ServiceUnavailableError:
				this.errorMessage = "Service currently unavailable.";
				break;
			case error instanceof HTTP.Errors.UnknownError:
				this.errorMessage = "An error occurred while trying to update the app. Please try again later. Error: " + error.response.status;
				break;
			default:
				this.errorMessage = "There was a problem processing the request. Error: " + error.response.status;
				break;
		}
	}

	closeMessage( evt:Event ):void {
		let message:JQuery = $( evt.srcElement ).closest( ".ui.message" );
		message.transition( {
			onComplete: ():void => {
				if( message.hasClass( "success" ) ) {
					this.displaySuccessMessage = false;
				} else {
					this.errorMessage = "";
				}
			},
		} ).transition( "fade" );
	}
}
