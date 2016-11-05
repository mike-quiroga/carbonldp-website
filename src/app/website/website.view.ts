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

	ngAfterViewInit():void {
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

	scrollTop():void {
		window.scroll( 0, 0 );
	}

}

export default WebsiteView;