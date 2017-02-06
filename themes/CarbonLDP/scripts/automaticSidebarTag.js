hexo.extend.helper.register( "automaticSidebarTag", ( pageContent ) => {
	let result = {
		html: "",
		error: null
	};

	jsDOM.env({
		html: pageContent,
		done: function (error, window) {
			if( error ) {
				result.error = error;
				return;
			}

			processDocContent( window ).then( () => {
				
				result.html = window.document.body.outerHTML;
				
			}).catch( ( error ) => {
				result.error = error;
				console.error( error );
			});
		}
	});

	deasync.loopWhile(() => ! result.html && ! result.error );

	return "<!DOCTYPE html>" + result.html;
});


function processDocContent( window ) {
	const document = window.document;
	const $ = document.querySelector.bind( document );
	const $$ = document.querySelectorAll.bind( document );

	const _ = {
		$: $,
		$$: $$
	};

	// let widgets = [
	// 	{
	// 		// <sidebar content-selector=".hello"></sidebar>
	// 		selector: "sidebar",
	// 		template: "<div></div>",
	// 		preRender: function() {
	//
	// 		}
	// 	},
	// 	{ selector: "tabs", template: "<div></div>" }
	// ];

	let widgets = glob.sync( hexo.theme.base + "widgets/*.js" ).map( ( file ) => {
		return require( path.resolve( file ) );
	});

	let widgetPromises = [];
	for( let widget of widgets ) {
		let elements = $$( widget.selector );
		if( elements.length === 0 ) continue;

		for (let i = 0; i < elements.length; ++i) {
			let element = elements[i];
			let widgetData = {};
			let widgetPromise = widget.preRender( element, widgetData, document, _ ).then( () => {
				// TODO: Use readFile instead of readFileSync
				// TODO: Reject the promise if there is no template or templateURL defined
				// TODO: Allow templateURL outside of the base (or even local to the widget file)
				let widgetTemplate = "template" in widget ? widget.template : "templateURL" in widget ? fs.readFileSync( hexo.theme.base + "widgets/" + widget.templateURL ).toString() : "";

				element.outerHTML = ejs.render( widgetTemplate, widgetData );
			} );

			widgetPromises.push( widgetPromise );
		}
	}

	return Promise.all( widgetPromises );
}