import { View, Component } from "@angular/core";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";

@Component( {
	selector: "modal",
	template: template
} )
export default class ModalComponent {
	ngAfterViewInit():void {
		$( ".ui.modal" ).modal( "show" );
	}
}
