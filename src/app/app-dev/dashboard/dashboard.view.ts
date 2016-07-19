import { Component } from "@angular/core";

import template from "./dashboard.view.html!";

@Component( {
	selector: "dashboard",
	template: template,
	styles: [ ":host { display:block; }" ],
} )
export class DashboardView {

	constructor() { }
}

export default DashboardView;
