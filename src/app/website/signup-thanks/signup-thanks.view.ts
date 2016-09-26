import { Component, AfterViewInit } from "@angular/core";

import "semantic-ui/semantic";

import template from "./signup-thanks.view.html!";
import style from "./signup-thanks.view.css!text";

@Component( {
	selector: "signup-thanks",
	template: template,
	styles: [ style ],
} )

export class SignupThanksView implements AfterViewInit {

	ngAfterViewInit():void {
		ga( "send", "event", "Newsletter", "Subscription" );
	}

}

export default SignupThanksView;
