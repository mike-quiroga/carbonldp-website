import {
	Component, View, Input, Output,
	CORE_DIRECTIVES,
	ElementRef
} from 'angular2/angular2';
import $ from 'jquery';
import 'semantic-ui/semantic';


@Component( {
	selector: 'resultset'
} )
@View( {
	directives: [ CORE_DIRECTIVES ],
	template: ""
} )

export class Resultset {
	static parameters = [ [ ElementRef ] ];
	element:ElementRef;
	$element:JQuery;

	table:HTMLTableElement;
	thead:HTMLTableElement;
	tbody:HTMLTableElement;

	@Input() resultset:any;
	@Input() outputformat:string;

	constructor( element:ElementRef ) {
		this.element = element;
	}

	afterViewInit():void {
		//console.log( "Resultset Table: %o", this.resultset );
		this.$element = $( this.element.nativeElement );

		switch ( this.outputformat.toLowerCase() ) {
			case SPARQLFormats.csv:
				this.buildCSV();
				break;
			case SPARQLFormats.xml:
				this.buildXML();
				break;
			case SPARQLFormats.json_ld:
				this.buildJSON_LD();
				break;
			case SPARQLFormats.json_rdf:
				this.buildJSOND_RDF();
				break;
			case SPARQLFormats.n3:
				this.buildN3();
				break;
			case SPARQLFormats.rdfxml:
				this.buildRDFXML();
				break;
			case SPARQLFormats.table:
				this.buildTable()
				break;
			case SPARQLFormats.tsv:
				this.buildTSV();
				break;
			case SPARQLFormats.turtle:
				this.buildTURTLE();
				break;
			case SPARQLFormats.bool:
				this.buildBool();
				break;
		}
	}

	buildCSV():void {
		console.log( SPARQLFormats.csv );
	}

	buildXML():void {
		console.log( SPARQLFormats.xml );
	}

	buildTSV():void {
		console.log( SPARQLFormats.tsv );
	}

	buildJSON_LD():void {
		console.log( SPARQLFormats.json_ld );
	}

	buildTURTLE():void {
		console.log( SPARQLFormats.turtle );
	}

	buildJSOND_RDF():void {
		console.log( SPARQLFormats.json_rdf );
	}

	buildRDFXML():void {
		console.log( SPARQLFormats.rdfxml );
	}

	buildN3():void {
		console.log( SPARQLFormats.n3 );
	}

	buildBool():void {
		console.log( SPARQLFormats.bool );
	}


	buildTable() {
		console.log( SPARQLFormats.table );
		this.table = <HTMLTableElement>document.createElement( "table" );
		this.thead = <HTMLTableElement> this.table.createTHead();
		this.tbody = <HTMLTableElement> this.table.createTBody();

		this.table.className = "ui celled very compact striped table";

		let columns:string[] = [];
		let td:HTMLElement = document.createElement( "tr" );
		// Loop through the heads in the data and add them to the table header
		this.resultset.head.vars.forEach( ( value:string, index:number ) => {
			//console.log( "%o %o", index, value );
			let th:HTMLElement = document.createElement( "th" );
			th.className = "center aligned";
			th.innerHTML = value;
			td.appendChild( th );
			// Add the variable name to the array for acccessing the body object properties
			columns.push( value );
		} );
		// Append the table header to the table
		this.thead.appendChild( td );
		this.table.appendChild( this.thead );

		let brow = <HTMLTableRowElement> this.tbody.insertRow( 0 );

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
			this.tbody.appendChild( tr );
		} );
		this.$element.html( "" );
		this.$element.append( this.table );
	}


	prefixURI( uri:string ):void {

		//for ( var prefix in Carbon.DefaultPrefixes ) {
		//	if ( Carbon.DefaultPrefixes.hasOwnProperty( prefix ) ) {
		//		var namespace = Carbon.DefaultPrefixes[prefix];
		//		if ( uri.lastIndexOf( namespace, 0 ) === 0 ) {
		//			return uri.replace( namespace, '<abbr title=&quot;' + namespace + '&quot;>' + prefix + '</abbr>:' );
		//		}
		//	}
		//}
	}
}
export enum SPARQLFormats {
	table = "table",
	xml = "xml",
	csv = "csv",
	tsv = "tsv",
	json_ld = "json-ld",
	turtle = "turtle",
	json_rdf = "json-rdf",
	rdfxml = "rdfxml",
	n3 = "n3",
	bool = "boolean"
}