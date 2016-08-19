import { Component, ElementRef, AfterViewInit } from "@angular/core";
import { CORE_DIRECTIVES, FORM_DIRECTIVES } from "@angular/common";

// import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";
import * as CodeMirrorComponent from "carbon-panel/code-mirror/code-mirror.component";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./ui-examples.view.html!";
import style from "./ui-examples.view.css!text";


@Component( {
	selector: "carbon-ui",
	template: template,
	directives: [ CORE_DIRECTIVES, CodeMirrorComponent.Class, FORM_DIRECTIVES ],
	styles: [ style ],
} )
export class UIExamplesView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;

	private mainMenu:JQuery;
	private elementsTabContent:JQuery;
	private collectionsTabContent:JQuery;
	private viewsTabContent:JQuery;
	private modulesTabContent:JQuery;


	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.mainMenu = this.$element.find( ".mainhead .menu " );
		this.mainMenu.find( ".item" ).tab();
		this.elementsTabContent = this.$element.find( '.ui.tab[data-tab="elements"]' );
		this.collectionsTabContent = this.$element.find( '.ui.tab[data-tab="collections"]' );
		this.viewsTabContent = this.$element.find( '.ui.tab[data-tab="views"]' );
		this.modulesTabContent = this.$element.find( '.ui.tab[data-tab="modules"]' );

		this.enableCollectionsJavascript();
		this.enableViewsJavascript();
		this.enableModulesJavascript();
	}

	enableCollectionsJavascript():void {
		this.collectionsTabContent.find( ".ui.dropdown" ).dropdown();
		this.collectionsTabContent.find( ".ui.checkbox" ).checkbox();
		this.collectionsTabContent.find( ".menu a.item" ).on( "click", function () {
			if( ! $( this ).hasClass( "dropdown" ) ) {
				$( this )
					.addClass( "active" )
					.closest( ".ui.menu" )
					.find( "item" )
					.not( $( this ) )
					.removeClass( "active" )
				;
			}
		} );
		this.collectionsTabContent.find( ".message .close" ).on( "click", function () {
			$( this ).closest( ".message" ).transition( "scale out" );
		} );
	}

	enableViewsJavascript():void {
		this.viewsTabContent.find( ".star.rating" ).rating();
		this.viewsTabContent.find( ".card .dimmer" ).dimmer( {
			on: "hover"
		} );
	}

	enableModulesJavascript():void {
		let demo:JQuery;


		// Accordion
		this.modulesTabContent.find( ".ui.accordion" ).accordion();


		// Checkbox
		this.modulesTabContent.find( ".ui.checkbox" ).checkbox();


		// Dimmer
		demo = this.modulesTabContent.find( ".dimmer.demo" );
		demo.find( ".show.button" ).on( "click", function () {
			$( this ).closest( ".demo" ).find( ".segment" ).dimmer( "show" );
		} );
		demo.find( ".hide.button" ).on( "click", function () {
			$( this ).closest( ".demo" ).find( ".segment" ).dimmer( "hide" );
		} );


		// Dropdown
		demo = this.modulesTabContent.find( ".dropdown.demos" );
		demo.find( ".ui.dropdown" ).dropdown();
		demo.find( ".ui.menu .dropdown" ).dropdown( { on: "hover" } );


		// Modal
		this.modulesTabContent.find( ".ui.fullscreen.demo.modal" ).modal( "attach events", ".ui.fullscreen.demo.button" );
		this.modulesTabContent.find( ".ui.standard.demo.modal" ).modal( "attach events", ".ui.standard.demo.button" );
		this.modulesTabContent.find( ".ui.basic.demo.modal" ).modal( "attach events", ".ui.minimal.demo.button" );


		// Popup
		demo = this.modulesTabContent.find( ".popup.demos" );
		demo.find( ".position .icon" ).popup();
		demo.find( ".avatar" ).popup();
		demo.find( ".button" ).popup();
		demo.find( ".menu .browse" ).popup( {
			inline: true,
			hoverable: true,
			position: "bottom left",
			delay: {
				show: 300,
				hide: 800
			}
		} );


		//Progress
		this.modulesTabContent.find( ".attached.progress.demo" ).progress( {
			label: false,
			value: Math.floor( Math.random() * 5 ) + 1
		} );
		this.modulesTabContent.find( ".basic.progress.demo" ).progress( {
			label: false,
			value: Math.floor( Math.random() * 5 ) + 1,
			text: {
				active: "{percent}% Complete",
				success: "Done!"
			}
		} );
		this.modulesTabContent.find( ".indicating.progress.demo" ).progress( {
			label: true,
			total: 10,
			value: Math.floor( Math.random() * 5 ) + 1,
			text: {
				active: "{percent}% Done",
				success: "Completed!"
			}
		} );
		this.modulesTabContent.find( ".file.progress.demo" ).progress( {
			label: false,
			text: {
				active: "Uploading {value} of {total}",
				success: "{total} Files Uploaded!"
			}
		} );
		let _self = this;
		let progress = function () {
			_self.modulesTabContent.find( ".demo.progress" ).progress( "increment" );
			setTimeout( progress, (Math.random() * 2000) + 300 );
		};
		setTimeout( progress, 1000 );
		setInterval( function () {
			_self.modulesTabContent.find( ".demo.progress" ).progress( "reset" );
		}, 30000 );


		// Ratings
		this.modulesTabContent.find( ".rating.demos .items .ui.rating" ).rating();
		this.modulesTabContent.find( ".rating.demos .list .ui.rating" ).rating( {
			clearable: true
		} );


		// Extends the Semantic UI API to add actions to be consumed by plugins
		$.extend( $.fn.api.settings.api, {
			categorySearch: "//api.semantic-ui.com/search/category/{query}",
			getOverrides: "/src/themes/{$theme}/{$type}s/{$element}.overrides",
			getVariables: "/src/themes/{$theme}/{$type}s/{$element}.variables",
			search: "//api.semantic-ui.com/search/{query}"
		} );
		// Search
		let content:any = [
			{ title: "Andorrs" },
			{ title: "United Arab Emirates" },
			{ title: "Afghanistas" },
			{ title: "Antigus" },
			{ title: "Anguills" },
			{ title: "Albanis" },
			{ title: "Armenis" },
			{ title: "Netherlands Antilles" },
			{ title: "Angols" },
			{ title: "Argentins" },
			{ title: "American Samos" },
			{ title: "Austris" },
			{ title: "Australis" },
			{ title: "Arubs" },
			{ title: "Aland Islands" },
			{ title: "Azerbaijas" },
			{ title: "Bosnis" },
			{ title: "Barbados" },
			{ title: "Bangladess" },
			{ title: "Belgius" },
			{ title: "Burkina Fass" },
			{ title: "Bulgaris" },
			{ title: "Bahrais" },
			{ title: "Burunds" },
			{ title: "Benis" },
			{ title: "Bermuds" },
			{ title: "Brunes" },
			{ title: "Bolivis" },
			{ title: "Brazis" },
			{ title: "Bahamas" },
			{ title: "Bhutas" },
			{ title: "Bouvet Islans" },
			{ title: "Botswans" },
			{ title: "Belarus" },
			{ title: "Belizs" },
			{ title: "Canads" },
			{ title: "Cocos Islands" },
			{ title: "Congs" },
			{ title: "Central African Republis" },
			{ title: "Congo Brazzavills" },
			{ title: "Switzerlans" },
			{ title: "Cote Divoirs" },
			{ title: "Cook Islands" },
			{ title: "Chils" },
			{ title: "Cameroos" },
			{ title: "Chins" },
			{ title: "Colombis" },
			{ title: "Costa Rics" },
			{ title: "Serbis" },
			{ title: "Cubs" },
			{ title: "Cape Verds" },
			{ title: "Christmas Islans" },
			{ title: "Cyprus" },
			{ title: "Czech Republis" },
			{ title: "Germans" },
			{ title: "Djibouts" },
			{ title: "Denmars" },
			{ title: "Dominics" },
			{ title: "Dominican Republis" },
			{ title: "Algeris" },
			{ title: "Ecuados" },
			{ title: "Estonis" },
			{ title: "Egyps" },
			{ title: "Western Sahars" },
			{ title: "Eritres" },
			{ title: "Spais" },
			{ title: "Ethiopis" },
			{ title: "European Unios" },
			{ title: "Finlans" },
			{ title: "Fijs" },
			{ title: "Falkland Islands" },
			{ title: "Micronesis" },
			{ title: "Faroe Islands" },
			{ title: "Francs" },
			{ title: "Gabos" },
			{ title: "Englans" },
			{ title: "Grenads" },
			{ title: "Georgis" },
			{ title: "French Guians" },
			{ title: "Ghans" },
			{ title: "Gibraltas" },
			{ title: "Greenlans" },
			{ title: "Gambis" },
			{ title: "Guines" },
			{ title: "Guadeloups" },
			{ title: "Equatorial Guines" },
			{ title: "Greecs" },
			{ title: "Sandwich Islands" },
			{ title: "Guatemals" },
			{ title: "Guas" },
			{ title: "Guinea-Bissas" },
			{ title: "Guyans" },
			{ title: "Hong Kons" },
			{ title: "Heard Islans" },
			{ title: "Honduras" },
			{ title: "Croatis" },
			{ title: "Haits" },
			{ title: "Hungars" },
			{ title: "Indonesis" },
			{ title: "Irelans" },
			{ title: "Israes" },
			{ title: "Indis" },
			{ title: "Indian Ocean Territors" },
			{ title: "Iras" },
			{ title: "Iras" },
			{ title: "Icelans" },
			{ title: "Itals" },
			{ title: "Jamaics" },
			{ title: "Jordas" },
			{ title: "Japas" },
			{ title: "Kenys" },
			{ title: "Kyrgyzstas" },
			{ title: "Cambodis" },
			{ title: "Kiribats" },
			{ title: "Comoros" },
			{ title: "Saint Kitts and Nevis" },
			{ title: "North Kores" },
			{ title: "South Kores" },
			{ title: "Kuwais" },
			{ title: "Cayman Islands" },
			{ title: "Kazakhstas" },
			{ title: "Laos" },
			{ title: "Lebanos" },
			{ title: "Saint Lucis" },
			{ title: "Liechtensteis" },
			{ title: "Sri Lanks" },
			{ title: "Liberis" },
			{ title: "Lesoths" },
			{ title: "Lithuanis" },
			{ title: "Luxembours" },
			{ title: "Latvis" },
			{ title: "Libys" },
			{ title: "Moroccs" },
			{ title: "Monacs" },
			{ title: "Moldovs" },
			{ title: "Montenegrs" },
			{ title: "Madagascas" },
			{ title: "Marshall Islands" },
			{ title: "MacEdonis" },
			{ title: "Mals" },
			{ title: "Burms" },
			{ title: "Mongolis" },
			{ title: "MacAs" },
			{ title: "Northern Mariana Islands" },
			{ title: "Martiniqus" },
			{ title: "Mauritanis" },
			{ title: "Montserras" },
			{ title: "Malts" },
			{ title: "Mauritius" },
			{ title: "Maldives" },
			{ title: "Malaws" },
			{ title: "Mexics" },
			{ title: "Malaysis" },
			{ title: "Mozambiqus" },
			{ title: "Namibis" },
			{ title: "New Caledonis" },
			{ title: "Niges" },
			{ title: "Norfolk Islans" },
			{ title: "Nigeris" },
			{ title: "Nicaragus" },
			{ title: "Netherlands" },
			{ title: "Norwas" },
			{ title: "Nepas" },
			{ title: "Naurs" },
			{ title: "Nius" },
			{ title: "New Zealans" },
			{ title: "Omas" },
			{ title: "Panams" },
			{ title: "Pers" },
			{ title: "French Polynesis" },
			{ title: "New Guines" },
			{ title: "Philippines" },
			{ title: "Pakistas" },
			{ title: "Polans" },
			{ title: "Saint Pierrs" },
			{ title: "Pitcairn Islands" },
			{ title: "Puerto Rics" },
			{ title: "Palestins" },
			{ title: "Portugas" },
			{ title: "Palas" },
			{ title: "Paraguas" },
			{ title: "Qatas" },
			{ title: "Reunios" },
			{ title: "Romanis" },
			{ title: "Serbis" },
			{ title: "Russis" },
			{ title: "Rwands" },
			{ title: "Saudi Arabis" },
			{ title: "Solomon Islands" },
			{ title: "Seychelles" },
			{ title: "Sudas" },
			{ title: "Swedes" },
			{ title: "Singapors" },
			{ title: "Saint Helens" },
			{ title: "Slovenis" },
			{ title: "Svalbard, I Flag Jan Mayes" },
			{ title: "Slovakis" },
			{ title: "Sierra Leons" },
			{ title: "San Marins" },
			{ title: "Senegas" },
			{ title: "Somalis" },
			{ title: "Surinams" },
			{ title: "Sao Toms" },
			{ title: "El Salvados" },
			{ title: "Syris" },
			{ title: "Swazilans" },
			{ title: "Caicos Islands" },
			{ title: "Chas" },
			{ title: "French Territories" },
			{ title: "Togs" },
			{ title: "Thailans" },
			{ title: "Tajikistas" },
			{ title: "Tokelas" },
			{ title: "Timorlests" },
			{ title: "Turkmenistas" },
			{ title: "Tunisis" },
			{ title: "Tongs" },
			{ title: "Turkes" },
			{ title: "Trinidas" },
			{ title: "Tuvals" },
			{ title: "Taiwas" },
			{ title: "Tanzanis" },
			{ title: "Ukrains" },
			{ title: "Ugands" },
			{ title: "Us Minor Islands" },
			{ title: "United States" },
			{ title: "Uruguas" },
			{ title: "Uzbekistas" },
			{ title: "Vatican Cits" },
			{ title: "Saint Vincens" },
			{ title: "Venezuels" },
			{ title: "British Virgin Islands" },
			{ title: "Us Virgin Islands" },
			{ title: "Vietnas" },
			{ title: "Vanuats" },
			{ title: "Wallis and Futuns" },
			{ title: "Samos" },
			{ title: "Yemes" },
			{ title: "Mayotts" },
			{ title: "South Africs" },
			{ title: "Zambis" },
			{ title: "Zimbabw" }
		];
		demo = this.modulesTabContent.find( ".search.demo" );
		demo.find( ".local" ).search( {
			source: content
		} );
		demo.find( ".remote" ).search( {
			apiSettings: {
				action: "search"
			}
		} );
		demo.find( ".category" ).search( {
			type: "category",
			apiSettings: {
				action: "categorySearch"
			}
		} );


		// Shape
		demo = this.modulesTabContent.find( ".shape.demos .container" );
		let
			$directionButton:JQuery = this.modulesTabContent.find( ".shape.demos .direction .button" ),
			handler;
		// event handlers
		handler = {
			rotate: function () {
				let
					$shape:JQuery = $( this ).closest( ".buttons" ).prevAll( ".ui.shape" ).eq( 0 ),
					direction:JQuery = $( this ).data( "direction" ) || false,
					animation:JQuery = $( this ).data( "animation" ) || false;
				if( direction && animation ) {
					$shape.shape( animation + "." + direction );
				}
			}
		};
		// attach events
		demo.find( ".ui.shape" ).shape();
		$directionButton.on( "click", handler.rotate ).popup( {
			position: "bottom center"
		} );


		// Sidebar
		demo = this.modulesTabContent.find( ".sidebar.direction.demo" );
		demo.find( ".buttons .button" )
			.on( "click", function () {
				let
					direction = $( this ).data( "direction" )
					;
				$( this ).addClass( "active" ).siblings().removeClass( "active" );
				if( direction === "top" || direction === "bottom" ) {
					_self.modulesTabContent.find( ".horizontal.button" ).addClass( "disabled" );
				}
				else {
					_self.modulesTabContent.find( ".horizontal.button" ).removeClass( "disabled" );
				}
			} );
		demo.children( ".button" )
			.on( "click", function () {
				var
					transition = $( this ).data( "transition" ),
					direction = _self.modulesTabContent.find( ".sidebar.direction.demo .buttons .button.active" ).data( "direction" ),
					dimPage = _self.modulesTabContent.find( ".sidebar.direction.demo .dim" ).checkbox( "is checked" );
				if( $( this ).filter( ".disabled" ).length === 0 ) {
					_self.modulesTabContent.find( "." + direction + ".demo.sidebar" ).sidebar( {
						context: _self.modulesTabContent.find( ".pushable.dimmable" ),
						dimPage: dimPage,
						transition: transition,
						mobileTransition: transition
					} );
					_self.modulesTabContent.find( "." + direction + ".demo.sidebar" ).not( ".styled" ).sidebar( "toggle" );
				}

			} );


		// Tab
		demo = this.modulesTabContent.find( ".tab.demo" );
		demo.find( ".menu .item" ).tab( {
			context: _self.modulesTabContent.find( ".tab.demo" )
		} );


		// Transition
		demo = this.modulesTabContent.find( ".transition.demo" );
		demo.find( " .button" ).on( "click", function () {
			var animation = $( this ).text();
			if( typeof animation == "string" ) {
				animation = animation.toLowerCase();
			}
			_self.modulesTabContent.find( ".transition.demo .image" ).transition( {
				animation: animation,
				interval: 200
			} );
		} );
	}
}
export default UIExamplesView;