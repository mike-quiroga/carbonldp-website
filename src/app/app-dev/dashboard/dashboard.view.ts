import { Component } from "@angular/core";
import { Title } from "@angular/platform-browser";

import template from "./dashboard.view.html!";


@Component( {
	selector: "dashboard",
	template: template,
	styles: [ ":host { display:block; }" ],
} )
export class DashboardView {

	private title:Title

	constructor(title:Title) {
		this.title = title;
	}

	routerOnActivate():void {
		this.title.setTitle( "App Dev | Dashboard" );
	}
}

export default DashboardView;
