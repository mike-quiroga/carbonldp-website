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
	private prevUrl = "";

	constructor( router:Router ) {
		this.router = router;
		this.router.events.subscribe( ( event:Event )=> {
			let url:string = "", scrollableContent:Element;
			if( event instanceof NavigationEnd ) {
				url = event.url;
				if( this.prevUrl !== url ) {
					scrollableContent = document.querySelector( ".scrollable-content" );
					if( scrollableContent )scrollableContent.scrollTop = 0;
					this.prevUrl = url;
				}
			}
		} );
	}

}

export default WebsiteView;