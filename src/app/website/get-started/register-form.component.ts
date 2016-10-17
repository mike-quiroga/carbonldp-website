import { Component } from '@angular/core';

import template from "./register-form.component.html!text";

@Component( {
	selector: 'register-form',
	template: template
} )

export class RegisterFormComponent {


	onSubmit( $event:any ):void {

		//console.log( $event );

		let icpForm = document.getElementById( 'icpsignup' );
		icpForm.action = "https://app.icontact.com/icp/core/mycontacts/signup/designer/form/?id=63&cid=581321&lid=23554";
		icpForm.submit();

	}

}
