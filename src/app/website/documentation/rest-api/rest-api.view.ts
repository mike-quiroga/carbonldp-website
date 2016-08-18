import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import template from "./rest-api.view.html!";
import style from "./rest-api.view.css!text";

@Component( {
	selector: "rest-api",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ]
} )

export class RESTApiView {
}

export default RESTApiView;
