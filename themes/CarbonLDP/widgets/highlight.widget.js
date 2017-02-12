const highlight = require( "highlight.js" );
const stripIndent = require( "strip-indent" );

module.exports = {
	selector: "pre > code",
	render: function( widget, data, element, page, document, _ ) {
		let source = element.innerHTML;
		source = stripIndent( source );

		// Remove first line if empty
		source = source.replace( /^[\t ]*\n/g, "" );

		// Remove second line if empty
		source = source.replace( /\n[\t ]*$/g, "" );

		source = source.replace( /\n[\t ]*$/g, "<br> " );

		element.innerHTML = source;

		highlight.configure( {
			tabReplace: "    ",
			classPrefix: "hljs-"
		} );

		// Register document as a global variable so highlight.js can access it
		global.document = document;

		highlight.highlightBlock( element );

		// Unregister document
		delete global.document;
	},
	styles: [
		{
			file: require.resolve( "highlight.js/styles/tomorrow-night.css" ),
			inline: true
		},
		{
			file: require.resolve( "./highlight.css" ),
			inline: true
		}
	]
};