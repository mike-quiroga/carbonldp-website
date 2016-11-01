import { Component, ViewEncapsulation, AfterViewInit } from "@angular/core";

import template from "./website.view.html!";
import style from "./website.view.css!text";

@Component( {
	selector: "website",
	template: template,
	encapsulation: ViewEncapsulation.None,
	styles: [ style ],
} )

export class WebsiteView implements AfterViewInit {

	ngAfterViewInit() {
		this.initializeScrollTopButton();
	}

	initializeScrollTopButton():void {
		let $scrollTopButton:JQuery = $( ".scroll-top > .ui.button" );
		let $window:JQuery = $( window );

		$window.scroll( function () {
			if( $window.scrollTop() ) {
				$scrollTopButton.fadeIn();
			} else {
				$scrollTopButton.fadeOut();
			}
		} );
	}

	scrollTop(){
		window.scroll( 0, 0 )
		return true;
	}

}

export default WebsiteView;