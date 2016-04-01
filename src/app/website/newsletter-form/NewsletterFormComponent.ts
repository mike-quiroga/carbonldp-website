import { Component } from "angular2/core";
import {Http, Headers, RequestOptions, Response} from "angular2/http";
import { CORE_DIRECTIVES, FORM_DIRECTIVES, FormBuilder, ControlGroup, AbstractControl, Validators } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router} from "angular2/router";

import { ValidationService } from "app/components/validation-service/ValidationService";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "newsletter-form",
	template: template,
	directives: [ CORE_DIRECTIVES, FORM_DIRECTIVES ]
} )

export class NewsletterFormComponent {
	http:Http;
	router:Router;
	subscribeForm:ControlGroup;
	email: AbstractControl;

	constructor( http:Http, router:Router, formBuilder:FormBuilder ) {
		this.http = http;
		this.router = router;
		this.subscribeForm = formBuilder.group( {
			"email": [ "", Validators.compose( [ Validators.required, ValidationService.emailValidator ] )],
			"listid": [ "777887" ],
			"specialid:777887": [ "MNSO" ],
			"clientid": [ "581321" ],
			"formid": [ "5139" ],
			"reallistid": [ "1" ],
			"doubleopt": [ "1" ]
		} );
		this.email = this.subscribeForm.controls[ "email" ];
	}



	onSubmit( body:string, valid:boolean, $event:any):void {
		let header:Headers = new Headers( {} );
		let options:RequestOptions = new RequestOptions( {header} );
		let icontactUrl:string = "https://app.icontact.com/icp/signup.php";

		this.email.markAsTouched();
		header.append( "Content-Type", "application/x-www-form-urlencoded" );

		if( valid && document.location.protocol === "https:" ) {
			this.http.post( icontactUrl, body, options )
				.map( ( res:Response ) => res.json() )
				.subscribe(
					data => {
						console.log( data );
					}
				);
			this.router.navigate( [ "/Website", "SignupThanks" ] );
		} else if ( valid ) {
			this.router.navigate( [ "/Website", "SignupThanks" ] );
		}
	}
}
