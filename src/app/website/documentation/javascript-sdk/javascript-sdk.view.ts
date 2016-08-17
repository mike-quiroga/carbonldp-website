import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import template from "./javascript-sdk.view.html!";
import style from "./javascript-sdk.view.css!text";

@Component( {
	selector: "javascript-sdk",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ],
} )

export class JavaScriptSDKView {
}

export default JavaScriptSDKView;
