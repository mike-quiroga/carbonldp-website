/// <reference path="./../../../../typings/typings.d.ts" />
import { Component, ElementRef, Input, Output, SimpleChange, EventEmitter } from "angular2/core";
import CodeMirror from "codemirror/lib/codemirror";

import "codemirror/mode/javascript/javascript";
import "codemirror/mode/sparql/sparql";
import "codemirror/mode/xml/xml";
import "codemirror/mode/turtle/turtle";

import "codemirror/lib/codemirror.css!";
import "codemirror/theme/mbo.css!";
import EventEmitter = webdriver.EventEmitter;

export class Mode {
	static get JAVASCRIPT():string { return "text/javascript"; }

	static get XML():string { return "application/xml"; }

	static get JSONLD():string { return "application/ld+json"; }

	static get JSONDRDF():string { return "application/json"; }

	static get N3():string { return "text/turtle"; }

	static get RDFXML():string { return "application/xml"; }

	static get CSV():string { return "text/plain"; }

	static get TSV():string { return "text/plain"; }

	static get TURTLE():string { return "text/turtle"; }

	static get SPARQL():string { return "application/sparql-query"; }
}

@Component( {
	selector: "code-mirror",
	template: "<ng-content></ng-content>"
} )
export class Class {
	element:ElementRef;
	codeMirror:CodeMirror;

	@Input() mode:string = Mode.JAVASCRIPT;
	@Input() readOnly:boolean = false;
	@Input() noCursor:boolean = false;
	@Input() showLineNumbers:boolean = true;
	@Input() scroll:boolean = true;

	@Input() value:string = "";
	@Output() valueChange:EventEmitter = new EventEmitter();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	ngOnDestroy() {
		this.element.nativeElement.innerHTML = this.codeMirror.getValue();
	}

	private getValue():string {
		let pres:any = this.element.nativeElement.querySelector( "pre" );
		if ( pres ) {
			if ( pres.length > 0 ) {
				// use everything inside the first pre
				return pres[ 0 ].innerHTML;
			} else {
				// use everything inside the pre
				return pres.innerHTML;
			}
		} else {
			// no pre"s, then use the everything inside code-mirror tag
			return this.element.nativeElement.innerHTML;
		}
		return "";
	}

	ngAfterViewInit():void {
		if ( ! this.value ) {
			this.value = this.getValue();
		}
		this.element.nativeElement.innerHTML = "";
		this.codeMirror = CodeMirror( this.element.nativeElement, {
			lineNumbers: this.showLineNumbers,
			indentWithTabs: true,
			smartIndent: false,
			electricChars: false,
			mode: this.mode,
			theme: "mbo",
			value: this.value,
			readOnly: this.readOnly
		} );
		if ( ! this.scroll ) {
			this.element.nativeElement.children[ 0 ].style.height = "auto";
		}

		this.codeMirror.on( "change", ( changeObject ) => {
			this.value = this.codeMirror.getValue();
			this.valueChange.next( this.value );
		} );
	}

	ngOnChanges( changeRecord:any ):void {
		if ( ! this.codeMirror ) return;

		if ( "readOnly" in changeRecord ) {
			let change:SimpleChange = changeRecord.readOnly;
			this.setReadOnly( change.currentValue );
		}

		if ( "noCursor" in changeRecord ) {
			let change:SimpleChange = changeRecord.noCursor;
			this.setNoCursor( change.currentValue );
		}

		if ( "value" in changeRecord ) {
			let change:SimpleChange = changeRecord.value;
			if ( change.currentValue !== this.codeMirror.getValue() ) this.codeMirror.setValue( change.currentValue );
		}

	}

	private setReadOnly( readOnly:boolean ):void {
		this.codeMirror.setOption( "readOnly", readOnly );
	}

	private setNoCursor( noCursor:boolean ):void {
		if ( noCursor ) this.codeMirror.setOption( "readOnly", "nocursor" );
		else this.setReadOnly( this.readOnly );
	}
}

export default Class;