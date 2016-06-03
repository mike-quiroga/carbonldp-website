import { Component, Input, Output, SimpleChange, EventEmitter } from "@angular/core";

import "semantic-ui/semantic";

import RelativizeURIPipe from "./RelativizeURIPipe";
import PrefixURIPipe from "./PrefixURIPipe";

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: "resultset-table",
	template: template,
	pipes: [ RelativizeURIPipe, PrefixURIPipe ],
} )

export class ResultsetTableComponent {
	@Input() query:any;
	@Input() resultset:any;
	@Input() prefixes:{ [ prefix:string ]:string };
	@Output() resultsetChange:EventEmitter<any> = new EventEmitter();

	sortedColumn:string = null;
	ascending:boolean = false;

	bindings:any;

	constructor() {}

	ngOnChanges( changeRecord:any ):void {
		if ( "resultset" in changeRecord ) {
			let change:SimpleChange = changeRecord.resultset;
			if ( change.currentValue !== change.previousValue ) {
				this.bindings = this.mapBindings( change.currentValue );
			}
		}
	}

	sortColumn( columnName:string ):void {
		if ( this.sortedColumn === columnName ) this.ascending = ! this.ascending;
		this.sortedColumn = columnName;

		let index:number = this.resultset.head.vars.indexOf( columnName );
		this.bindings.sort( ( bindingA, bindingB ) => {
			if ( bindingA[ index ].value > bindingB[ index ].value ) return this.ascending ? - 1 : 1;
			if ( bindingA[ index ].value < bindingB[ index ].value ) return this.ascending ? 1 : - 1;
			return 0;
		} );
	}

	private mapBindings( resultset:any ):any {
		return resultset.results.bindings.map( ( bindingObject ) => {
			let bindingArray:any = [];
			for ( let varName of resultset.head.vars ) {
				bindingArray.push( bindingObject[ varName ] );
			}
			return bindingArray;
		} );
	}
}
