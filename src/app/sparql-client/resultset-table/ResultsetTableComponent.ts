import {
	Component, Input, Output, SimpleChange, EventEmitter
	CORE_DIRECTIVES,
	ElementRef
} from 'angular2/angular2';
import $ from 'jquery';
import 'semantic-ui/semantic';

import template from "./template.html!";
import "./style.css!";

@Component( {
	selector: 'resultset-table',
	directives: [ CORE_DIRECTIVES ],
	template: template
} )

export class ResultsetTableComponent {

	static parameters = [ [ ElementRef ] ];
	element:ElementRef;
	$element:JQuery;

	table:HTMLTableElement;
	tableHead:HTMLTableElement;
	tableBody:HTMLTableElement;

	@Input() resultset:any;
	@Output() resultsetChange:EventEmitter = new EventEmitter();

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		this.$element = $( this.element.nativeElement );
		if ( this.resultset != null && this.resultset.head != null )
			this.buildTable();
	}

	onChanges( changeRecord:any ):void {
		if ( "resultset" in changeRecord ) {
			let change:SimpleChange = changeRecord.resultset;
			if ( change.currentValue !== change.previousValue ) {
				this.resultset = change.currentValue;
				this.buildTable();
			}
		}
	}

	buildTable() {
		this.table = <HTMLTableElement>document.createElement( "table" );
		this.tableHead = <HTMLTableElement> this.table.createTHead();
		this.tableBody = <HTMLTableElement> this.table.createTBody();

		this.table.className = "ui celled very compact striped table";

		let columns:string[] = [];
		let td:HTMLElement = document.createElement( "tr" );
		// Loop through the heads in the data and add them to the table header
		this.resultset.head.vars.forEach( ( value:string, index:number ) => {
			let th:HTMLElement = document.createElement( "th" );
			th.className = "center aligned";
			th.innerHTML = value;
			td.appendChild( th );
			// Add the variable name to the array for accessing the body object properties
			columns.push( value );
		} );
		// Append the table header to the table
		this.tableHead.appendChild( td );
		this.table.appendChild( this.tableHead );


		// Variables to help with the class alternating
		let resourcesNumber:number = 0;
		let lastResourceURI:string = "";


		// Loop through the rows of the result set
		this.resultset.results.bindings.forEach( ( result:any, index:number ) => {
			// Create a table row
			let tr:HTMLElement = document.createElement( "tr" );

			// Loop through the row cells
			columns.forEach( ( colName:string, colIndex:number ) => {
				// Create a table cell
				let td:HTMLElement = document.createElement( "td" );

				var firstURI:boolean = false;
				// If it is the first URI get the resourceURI
				if ( colIndex == 0 ) {
					if ( result[ colName ].value != lastResourceURI ) {
						lastResourceURI = result[ colName ].value;
						resourcesNumber ++;
						firstURI = true;
					}
				}


				// If the row doesn't have the column make it empty
				if ( result[ colName ] == null ) {
					td.innerHTML = "";
				} else {
					var type = result[ colName ].type;
					var value = result[ colName ].value;

					// Check the type of information the cell has and add the content to it
					if ( type == "uri" ) {
						if ( firstURI ) firstURI = false;
						else {
							if ( value.search( lastResourceURI ) != - 1 ) {
								value = value.replace( lastResourceURI, "&#60;" ) + "&#62;";
							}
						}
					} else if ( type == "typed-literal" ) {
						//var datatype = prefixURI( result[ colName ].datatype )?;
						var datatype = "";
						value = '"' + value + '" ^^ ' + datatype;
						td.className = td.className + " success";
					} else {
						value = '"' + value + '"';
						td.className = td.className + " warning";
					}
					//value = prefixURI( value );
					td.innerHTML = value;
				}

				// Add the cell to the row
				tr.appendChild( td );
			} );

			// Add the cell to the row
			this.tableBody.appendChild( tr );
		} );
		this.element.nativeElement.innerHTML = "";
		this.element.nativeElement.appendChild( this.table );
	}
}