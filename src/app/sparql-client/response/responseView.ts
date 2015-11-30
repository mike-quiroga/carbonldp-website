import {
	Component, View, Input, Output,
	CORE_DIRECTIVES, FORM_DIRECTIVES,
	ElementRef, SimpleChange, NgClass
} from "angular2/angular2";
import $ from "jquery";
import "semantic-ui/semantic";
import template from "./template.html!";
import "./style.css!";
import {Resultset} from "../resultset/resultset";
import SPARQLClientComponent from "../SPARQLClientComponent";

@Component( {
	selector: 'sparql-response'
} )
@View( {
	directives: [ CORE_DIRECTIVES, NgClass, Resultset ],
	template: template
} )
export class SPARQLResponse {
	static parameters = [ [ ElementRef ] ];


	element:ElementRef;
	$element:JQuery;

	@Input() response:SPARQLResponse;
	@Input() index:number;
	@Input() responses:SPARQLResponse[];

	accordion:any;
	menu:any;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.accordion = this.$element.find( '.accordion' );
		this.accordion.accordion( {
			selector: {
				trigger: '.title .btn-toggle'
			}
		} );
		this.menu = this.$element.find( '.content .tabular.menu > .item' );
		this.menu.tab();
		console.log( this.response );
	}

	onClose():void {
		this.responses[ this.index ] = <SPARQLResponse>{};
		this.responses.splice( this.index, 1 )
	}
}
export enum SPARQLResponseType {
	success = "success",
	default = "default",
	error = "error"
}