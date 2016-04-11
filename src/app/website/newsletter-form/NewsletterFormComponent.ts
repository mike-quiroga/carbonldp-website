import { Component, Inject } from "angular2/core";
import {Http, Headers, RequestOptions, Response} from "angular2/http";
import { CORE_DIRECTIVES, FORM_DIRECTIVES } from "angular2/common";
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
	show:boolean = false;
	emailRequired:boolean = false;
	emailInvalid: boolean = false;
	redirectPage = document.location.href + "/signup-thanks/"   ;
	//redirectPage = "https://local.carbonldp.com/carbon-website/src/";
	errorPage = document.location.href;
	//errorPage = "https://local.carbonldp.com/carbon-website/src/";
	constructor( http:Http, router:Router ) {
		this.http = http;
		this.router = router;
	}


	onSubmit( email:HTMLElement ):void {
		let icpForm:HTMLElement = document.getElementById( 'icpsignup' );
		let valid:any = ValidationService.emailValidator(email);
		this.emailInvalid = false;
		this.emailRequired = false;
		if ( valid === null ) {
			this.show = false;
			//if ( document.location.protocol === "https:" ) {
				icpForm.action = "https://app.icontact.com/icp/signup.php";
				icpForm.submit();
			//}
		} else {
			this.show = true;
			if( email.value != "" ){
				this.emailInvalid = true;
 			} else {
				this.emailRequired = true;
			}
		}

	}
}
