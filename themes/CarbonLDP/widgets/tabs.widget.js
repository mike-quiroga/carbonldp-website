function randomString( length, chars ) {
	let mask = "";
	if( chars.indexOf( "a" ) > - 1 ) mask += "abcdefghijklmnopqrstuvwxyz";
	if( chars.indexOf( "A" ) > - 1 ) mask += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	if( chars.indexOf( "#" ) > - 1 ) mask += "0123456789";
	if( chars.indexOf( "!" ) > - 1 ) mask += "~`!@#$%^&*()_+-={}[]:\";'<>?,./|\\";

	let result = "";
	for( let i = length; i > 0; -- i ) result += mask[ Math.floor( Math.random() * mask.length ) ];

	return result;
}

module.exports = {
	selector: "tabs",
	templateURL: "tabs.ejs",
	preRender: function( widget, data, element, page, document, _ ) {
		return Promise.resolve().then( () => {
			let tabElements = element.children;
			data.tabs = [];
			data.activeTab = 0;

			let foundActiveTab = false;
			for( let i = 0; i < tabElements.length; i ++ ) {
				let tabElement = tabElements[ i ];
				if( tabElement.tagName !== "TAB" ) throw new Error( `The 'tabs' widget only allows 'tab' elements. Found ${tabElement.tagName}` );

				let tab = {};
				tab.title = tabElement.getAttribute( "title" );
				tab.content = tabElement.innerHTML;
				tab.id = tabElement.getAttribute( "name" ) ? tabElement.getAttribute( "name" ) : randomString( 8, "aA" );

				tab.active = ! ! tabElement.getAttribute( "active" );
				if( tab.active ) {
					if( foundActiveTab ) throw new Error( "Two 'tabs' where marked as active" );
					foundActiveTab = true;
					data.activeTab = i;
				}

				data.tabs.push( tab );
			}

			if( data.tabs.length > 0 && data.activeTab === 0 ) data.tabs[ 0 ].active = true;
		} );
	}, styles: [
		{
			file: require.resolve( "./tabs.css" ),
			inline: true
		}
	],
	scripts: [
		{
			sourceURL: require.resolve( "./tabs.js" )
		}
	]
};