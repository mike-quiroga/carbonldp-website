import { Component, ElementRef } from "angular2/core";
import { CORE_DIRECTIVES, NgForm } from "angular2/common";
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from "angular2/router";
import { Title } from "angular2/platform/browser";
import { NewsletterFormComponent } from "../newsletter-form/NewsletterFormComponent";

import $ from "jquery";
import "semantic-ui/semantic";
import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "home",
	template: template,
	directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class, NewsletterFormComponent ],
	providers: [ Title ]
} )
export default class HomeView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	$mainMenu:JQuery;
	$articles:JQuery;
	$carbonLogo:JQuery;
	title:Title;

	constructor( router:Router, element:ElementRef, title:Title ) {
		this.router = router;
		this.element = element;
		this.title = title;
		this.title.setTitle("Home");
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.$mainMenu = $( "header > .menu" );
		this.$articles = $("#articles").find(".column");
		 // This.$carbonLogo = this.$element.find( "carbon-logo" );
		this.$carbonLogo = this.$element.find( "img.carbon-logo" );

		this.hideMainMenu();
		this.createDropdownMenus();
		this.addMenuVisibilityHandlers();
		this.createAccordions();
	}

	routerOnDeactivate():void {
		this.removeMenuVisibilityHandlers();
		this.showMainMenu();
	}

	isActive( route:string ):boolean {
		let instruction:any = this.router.generate( [ route ] );
		return this.router.isRouteActive( instruction );
	}

	showMainMenu():void {
		if ( this.$mainMenu.is( ":visible" ) ) return;
		this.toggleMainMenu();
	}

	hideMainMenu():void {
		if ( ! this.$mainMenu.is( ":visible" ) ) return;
		this.toggleMainMenu();
	}

	toggleMainMenu():void {
		this.$mainMenu.transition( "fade down" );
	}

	createDropdownMenus():void {
		this.$element.find( ".ui.dropdown" ).dropdown( {
			on: "hover"
		} );
	}

	addMenuVisibilityHandlers():void {
		let view:HomeView = this;
		this.$carbonLogo.visibility( {
			once: false,
			onBottomPassedReverse: function():void {
				view.hideMainMenu();
			},
			onBottomPassed: function():void {
				view.showMainMenu();
			}
		} );
		this.$articles.visibility({
			once: false,
			onTopVisible: function():void {
				view.addTextAnimation();
			}
		} );
	}

	removeMenuVisibilityHandlers():void {
		this.$carbonLogo.visibility( "destroy" );
	}

	scrollTo( event:any):boolean {
		let
			id:string = $( event.srcElement).attr( "href" ).replace( "#", "" ),
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
		this.$articles.find("p").transition( "scale in" );
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}


/*	newsletterSignUp(){
		let icpForm5139 = $("#icpsignup5139");
		let protocol = location.protocol;
		let test = document.getElementById("icpsingup5139");

		icpForm5139.action = "https://app.icontact.com/icp/signup.php";
		$('form').submit(function() {

			if (icpForm5139.find("#fields_email").value == "") {
				//icpForm5139["fields_email"].focus();
				$('p.spam').text('Please enter a valid e-mail');
				return false;
			}
			return true;

		});
		console.log("SignUp to newsletter: ");
		console.log(icpForm5139);
		console.log(test);
		console.log(protocol);
	}*/
}
