import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { Router } from "@angular/router";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./home.view.html!";
import style from "./home.view.css!text";

@Component( {
	selector: "home",
	template: template,
	styles: [ style ],
} )

export class HomeView implements AfterViewInit {
	private router:Router;
	private element:ElementRef;
	private $element:JQuery;
	private $mainMenu:JQuery;
	private $articles:JQuery;

	constructor( router:Router, element:ElementRef ) {
		this.router = router;
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$mainMenu = $( "header > .menu" );
		this.$articles = $( "#articles" ).find( ".column" );

		this.createDropdownMenus();
		this.addMenuVisibilityHandlers();
		this.createAccordions();
	}

	createDropdownMenus():void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover"
		} );
	}

	addMenuVisibilityHandlers():void {
		this.$articles.visibility( {
			once: false,
			onTopVisible: ():void => {
				this.addTextAnimation();
			}
		} );
	}

	removeMenuVisibilityHandlers():void {
		this.$articles.visibility( "destroy" );
	}

	scrollTo( event:any ):boolean {
		let
			id:string = $( event.srcElement ).attr( "href" ).replace( "#", "" ),
			$element:JQuery = $( "#" + id ),
			position:number = $element.offset().top - 80
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

	addTextAnimation():void {
		let paragraphs:JQuery = this.$articles.find( "p" );
		paragraphs.transition( "scale in" );
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

}

export default HomeView;