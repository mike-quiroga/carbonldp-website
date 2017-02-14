module.exports = {
	selector: "breadcrumb",
	templateURL: "breadcrumb.ejs",
	preRender: ( widget, data, element, page, document, _ ) => {
		// Read attribute "content-selector"
		// querySelector to load content
		// Process content to generate sidebar

		let path = document.location[ "pathname" ].split( "/" );
		console.log( document.location.pathname );
		console.log( hexo.route.get() );
		return Promise.resolve();
	},
	// styles: [
	// 	{
	// 		file: require.resolve( "./sidebar.css" ),
	// 		inline: true
	// 	}
	// ]
};
