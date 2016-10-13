import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./rest-api.view.html!";
import style from "./rest-api.view.css!text";

@Component( {
	selector: "rest-api",
	template: template,
	styles: [ style ],
} )

export class RESTApiView {
}

export default RESTApiView;
