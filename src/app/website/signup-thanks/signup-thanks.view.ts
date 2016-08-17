import { Component, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import "semantic-ui/semantic";

import template from "./signup-thanks.view.html!";
import style from "./signup-thanks.view.css!text";

@Component( {
	selector: "signup-thanks",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	styles: [ style ],
} )

export class SignupThanksView implements AfterViewInit {

	ngAfterViewInit():void {
		ga( "send", "event", "Newsletter", "Subscription" );
	}

}

export default SignupThanksView;
