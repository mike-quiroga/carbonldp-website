import { Component, Input, Output, ElementRef, SimpleChange, EventEmitter } from 'angular2/angular2';

import CodeMirror from 'codemirror/lib/codemirror';

import 'codemirror/mode/javascript/javascript';
import 'codemirror/mode/sparql/sparql';

import 'codemirror/lib/codemirror.css!';
import 'codemirror/theme/mbo.css!';
import EventEmitter = webdriver.EventEmitter;

export class Mode {
	static get JAVASCRIPT():string { return "javascript"; }

	static get SPARQL():string { return "sparql"; }
}

@Component( {
	selector: 'code-mirror',
	template: ''
} )
export class Class {
	static parameters = [ [ ElementRef ] ];

	element:ElementRef;
	codeMirror:CodeMirror;

	@Input() mode:string = Mode.JAVASCRIPT;
	@Input() readOnly:boolean = false;
	@Input() noCursor:boolean = false;

	@Input() value:string = "";
	@Output() valueChange:EventEmitter = new EventEmitter();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		// Clear of existing content inside code mirror
		(<HTMLElement>document.querySelector( "code-mirror" )).innerHTML = "";
		this.codeMirror = CodeMirror( this.element.nativeElement, {
			lineNumbers: true,
			indentWithTabs: true,
			smartIndent: false,
			electricChars: false,
			mode: this.mode,
			theme: "mbo",
			value: this.value
		} );

		this.codeMirror.on( "change", ( changeObject ) => {
			this.value = this.codeMirror.getValue();
			this.valueChange.next( this.value );
		} );
	}

	onChanges( changeRecord:any ):void {
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