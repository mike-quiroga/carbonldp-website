module.exports = {
	selector: "staticContentMenu",
	templateURL: "staticContentMenu.ejs",
	preRender: ( widget, data, element, page, document, _ ) => {
		// Read attribute "content-selector"
		// querySelector to load content
		// Process content to generate sidebar
		data.selector = element.getAttribute( "content" );
		data.content = getContent( element, data.selector, _ );

		if( data.content === null ) {
			data.sectionsTree = [];
			return Promise.resolve();
		}

		data.firstLevelChildren = data.content.children;

		data.sectionsTree = buildSectionsTree( data.firstLevelChildren );

		return Promise.resolve();
	},
	styles: [
		{
			file: require.resolve( "./staticContentMenu.css" ),
			inline: true
		}
	],
	scripts: [
		{
			sourceURL: require.resolve( "./staticContentMenu.js" )
		}
	]
};


function getContent( element, selector, _ ) {
	if( selector === null ) return null;

	return _.$( "." + selector );
}


function buildSectionsTree( nodeList ) {
	var sectionsTree = [];
	for( let i = 0, listLength = nodeList.length; i < listLength; i ++ ) {

		if( nodeList[ i ].tagName !== "SECTION" ) continue;

		let section = []

		section[ "html" ] = nodeList[ i ];
		section[ "id" ] = getId( nodeList[ i ] );
		section[ "title" ] = getTitle( nodeList[ i ] );
		section[ "id" ] = setId( section );

		let nextLevelChildren = nodeList[ i ].children;

		if( ! nextLevelChildren && nodeList[ i ].tagName !== "SECTION" ) continue;

		section[ "subsections" ] = buildSectionsTree( nextLevelChildren );
		sectionsTree.push( section );

	}


	return sectionsTree;
}

function getTitle( element ) {
	let title = "section";
	let titleElement = element.querySelector( "h1,h2,h3,h4,h5,h6" );

	if( titleElement === null ) return title;

	title = titleElement.textContent;

	return title;
}

function getId( element ) {
	return element.getAttribute( "id" );
}


function setId( element ) {
	if( element[ "id" ] !== null && element[ "id" ] !== "" ) return element[ "id" ];

	let id = element[ "title" ].toLowerCase().replace( /[\W_]+/g, " " );
	id = id.split( " " ).join( "-" );

	element[ "html" ].setAttribute( "id", id );

	return id;
}


