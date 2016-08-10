import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES, NgForm } from "@angular/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "@angular/router-deprecated";
import { Title } from "@angular/platform-browser";
import { NewsletterFormComponent } from "../newsletter-form/NewsletterFormComponent";

import $ from "jquery";
import "semantic-ui/semantic";
// import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";

import template from "./template.html!";
import style from "./style.css!text";

@Component( {
	selector: "home",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class, NewsletterFormComponent ],
	providers: [ Title ]
} )
export default class HomeView {
	router: Router;
	element: ElementRef;
	$element: JQuery;
	$mainMenu: JQuery;
	$articles: JQuery;
	title: Title;
	$carbonLogo: JQuery;

	constructor( router: Router, element: ElementRef, title: Title ) {
		this.router = router;
		this.element = element;
		this.title = title;
	}

	ngAfterViewInit(): void {
		this.$element = $( this.element.nativeElement );
		this.$mainMenu = $( "header > .menu" );
		this.$articles = $( "#articles" ).find( ".column" );

		this.createDropdownMenus();
		this.addMenuVisibilityHandlers();
		this.createAccordions();
	}

	routerOnActivate(): void {
		this.title.setTitle( "CarbonLDP | Home" );
	}

	routerOnDeactivate(): void {
		this.removeMenuVisibilityHandlers();
	}

	isActive( route: string ): boolean {
		let instruction: any = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	createDropdownMenus(): void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover"
		} );
	}

	addMenuVisibilityHandlers(): void {
		this.$articles.visibility( {
			once: false,
			onTopVisible: (): void => {
				this.addTextAnimation();
			}
		} );
	}

	removeMenuVisibilityHandlers(): void {
		this.$articles.visibility( "destroy" );
	}

	scrollTo( event: any ): boolean {
		let
			id: string = $( event.srcElement ).attr( "href" ).replace( "#", "" ),
			$element: JQuery = $( "#" + id ),
			position: number = $element.offset().top - 80
			;
		$element.addClass( "active" );
		$( "html, body" ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = "#" + id;
		event.stopImmediatePropagation();
		event.preventDefault();
		return false;
	}

	addTextAnimation(): void {
		let paragraphs: JQuery = this.$articles.find( "p" );
		paragraphs.transition( "scale in" );
	}

	createAccordions(): void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

}
