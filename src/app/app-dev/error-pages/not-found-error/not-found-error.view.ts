import { Component } from "@angular/core";
import { Title } from "@angular/platform-browser";

import template from "./not-found-error.view.html!";
import style from "./not-found-error.view.css!text";

@Component( {
	selector: "dashboard",
	template: template,
	styles: [ style ],
} )
export class NotFoundErrorView {

	private title:Title;

	constructor(title:Title) {
		this.title = title;
	}

	routerOnActivate(){
		this.title.setTitle( "Error | 404" );
	}
}

export default NotFoundErrorView;
