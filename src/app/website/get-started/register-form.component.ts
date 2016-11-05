import { Component, ViewChild } from "@angular/core";
import { NgForm, AbstractControl } from "@angular/forms";

import template from "./register-form.component.html!text";

@Component( {
	selector: "register-form",
	template: template
} )

export class RegisterFormComponent {
	@ViewChild( NgForm ) registerForm:NgForm;

	onSubmit( $event:any ):void {
		let icpForm:HTMLFormElement = <any> document.getElementById( "icpsignup" );
		icpForm.action = "https://app.icontact.com/icp/core/mycontacts/signup/designer/form/?id=63&cid=581321&lid=23554";
		icpForm.submit();
	}
}
