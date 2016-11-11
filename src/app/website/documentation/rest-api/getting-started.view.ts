import { Component, ElementRef, AfterViewInit } from "@angular/core";

import Carbon from "carbonldp/Carbon";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./getting-started.view.html!";

@Component( {
	selector: "getting-started-rest-api",
	template: template,
} )
export class GettingStartedView implements AfterViewInit {
	private element:ElementRef;
	private $element:JQuery;
	private protocolAndHost:string;

	private carbon:Carbon;
	private contentReady:boolean = false;

	private selectedLanguage:number = 0;

	private languages:{ [ name:string ]:number } = {
		trig: 0,
		jsonld: 1
	};

	constructor( element:ElementRef, carbon:Carbon ) {
		this.element = element;

		this.carbon = carbon;

		this.protocolAndHost = `${ this.carbon.getSetting( "http.ssl" ) ? "https" : "http" }://${ this.carbon.getSetting( "domain" ) }`;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
		window.setTimeout( () => this.contentReady = true, 0 );
		this.callPostmanGetButton();
	}

	createAccordions():void {
		this.$element.find( ".ui.accordion" ).accordion();
	}

	selectLanguage( language:number ):void {
		this.selectedLanguage = language;
	}

	private callPostmanGetButton():void {
		(function( p, o, s, t, m, a, n ) {
			! p[ s ] && (p[ s ] = function() { (p[ t ] || (p[ t ] = [])).push( arguments ); });
			! o.getElementById( s + t ) && o.getElementsByTagName( "head" )[ 0 ].appendChild( (
				(n = o.createElement( "script" )),
					(n.id = s + t), (n.async = 1), (n.src = m), n
			) );
		}( window, document, "_pm", "PostmanRunObject", "https://run.pstmn.io/button.js" ));
	}
}

export default GettingStartedView;
