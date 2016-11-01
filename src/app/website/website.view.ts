import { Component, ViewEncapsulation } from "@angular/core";
import { Router, NavigationEnd, Event } from "@angular/router";

import template from "./website.view.html!";
import style from "./website.view.css!text";

@Component( {
	selector: "website",
	template: template,
	encapsulation: ViewEncapsulation.None,
	styles: [ style ],
} )

export class WebsiteView {

}

export default WebsiteView;