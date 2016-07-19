import { Component } from "@angular/core";

import template from "./footer.component.html!";
import style from "./footer.component.css!text";

@Component( {
	selector: "footer",
	styles: [ style ],
	template: template,
} )
export class FooterComponent {

	constructor() { }
}

export default FooterComponent;
