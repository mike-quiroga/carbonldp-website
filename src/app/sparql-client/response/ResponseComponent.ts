import {
	Component, Input, Output,
	CORE_DIRECTIVES, FORM_DIRECTIVES,
	ElementRef, EventEmitter, SimpleChange, NgClass
} from "angular2/angular2";

import { ResultsetComponent } from "../resultset/ResultsetComponent";
import SPARQLClientComponent from "../SPARQLClientComponent";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: 'sparql-response',
	directives: [ CORE_DIRECTIVES, NgClass, ResultsetComponent ],
	template: template
} )
export class ResponseComponent {
	static parameters = [ [ ElementRef ] ];


	element:ElementRef;
	$element:JQuery;

	@Input() response:ResponseComponent;
	@Output() onRemove:EventEmitter = new EventEmitter();

	accordion:any;
	menu:any;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.accordion = this.$element.find( '.accordion' );
		this.accordion.accordion( {
			onOpen: () => {
				this.onOpen();
			},
			selector: {
				trigger: '.title .btn-toggle'
			}
		} );
		this.menu = this.$element.find( '.content .tabular.menu > .item' );
		this.menu.tab();
	}

	onRemoveResponse():void {
		this.onRemove.next( this.response );
	}

	onOpen():void {
		this.$element.find( '.CodeMirror' ).each( function ( i, element ) {
			element.CodeMirror.refresh();
		} );
	}
}
export class SPARQLResponseType {
	static success:string = "success";
	static default:string = "default";
	static error:string = "error";
}