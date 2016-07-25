import { Component } from "@angular/core";
import { ROUTER_DIRECTIVES } from "@angular/router-deprecated";

import template from "./javascript-sdk.view.html!";

@Component( {
	selector: "javascript-sdk",
	template: template,
	directives: [ ROUTER_DIRECTIVES ],
} )
export class JavaScriptSDKView {

}

export default JavaScriptSDKView;
