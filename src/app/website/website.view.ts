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
	private router:Router;

	constructor( router:Router ) {
		this.router = router;
	}

	ngAfterViewInit(){
		$(window).scroll(function() {
			console.log("scrolling");
			if ($(this).scrollTop()) {
				$(".scroll-top > .ui.button").fadeIn();
			} else {
				$(".scroll-top > .ui.button").fadeOut();
			}
		});
	}

}

export default WebsiteView;