import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./javascript-sdk.view.html!";
import style from "./javascript-sdk.view.css!text";

@Component( {
	selector: "javascript-sdk",
	template: template,
	styles: [ style ],
} )

export class JavaScriptSDKView {
}

export default JavaScriptSDKView;
