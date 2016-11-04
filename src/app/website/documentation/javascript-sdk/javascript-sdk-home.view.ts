import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./javascript-sdk-home.view.html!";
import style from "./javascript-sdk-home.view.css!text";

@Component( {
	selector: "javascript-sdk-home",
	template: template,
	styles: [ style ],
} )

export class JavaScriptSDKHomeView {
}

export default JavaScriptSDKHomeView;
