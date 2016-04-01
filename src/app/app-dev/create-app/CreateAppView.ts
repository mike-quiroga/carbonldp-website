import { Component, ElementRef, Type, Input, SimpleChange } from "angular2/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Control, Validators } from "angular2/common";
import { Router, RouteDefinition, ROUTER_DIRECTIVES, CanActivate} from "angular2/router";
import { Observable } from "rxjs";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";

import $ from "jquery";
import "semantic-ui/semantic";

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
	sending:boolean = false;
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
		return (! this.name.pristine && ! this.name.valid) || (! this.slug.pristine && ! this.slug.valid) || (! this.description.pristine && ! this.description.valid) || ! ! this.errorMessage;
	}

	onSubmit( data:{ name:string, slug:string, description:string }, $event:any ):void {
		$event.preventDefault();
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
}
