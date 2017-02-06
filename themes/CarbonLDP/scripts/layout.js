const deasync = require( "deasync" );
const jsDOM = require( "jsdom" );
const ejs = require( "ejs" );
const glob = require( "glob" );
const path = require( "path" );
const fs = require( "fs" );

hexo.extend.helper.register( "layout", ( html, sidebarEnabled ) => {
	let result = {
		html: "",
		error: null
	};

	jsDOM.env({
		html: html,
		done: function (error, window) {
			if( error ) {
				result.error = error;
				return;
			}

			processDOM( window, sidebarEnabled ).then( () => {
				result.html = window.document.querySelector( "html" ).outerHTML;
			}).catch( ( error ) => {
				result.error = error;
				console.error( error );
			});
		}
	});

	deasync.loopWhile(() => ! result.html && ! result.error );

	return "<!DOCTYPE html>" + result.html;
	
	// // Start of jsdom-global, virtual DOM access
	// const closeDOM = require( "jsdom-global" )( pageContent );
	// // const stripIndent = require( "strip-indent" );
	//
	//
	// //codeblocks builder
	//
	// if( sidebarEnabled ) renderSidebar( pageContent );
	//
	// closeDOM();
	//
	// pageContent += '</div>';
	// return pageContent;
} );

function processDOM( window, sidebarEnabled ) {
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









function renderSidebar( pageContent ) {
	let sidebarContent = '';
	let allSections = document.querySelectorAll( "section" );

	for( let i = 0, length = allSections.length; i < length; i ++ ) {
		if( allSections[ i ].parentNode !== document.body ) continue;

		let sections = allSections[ i ].querySelectorAll( ".ui.header" );
		if( typeof sections === "undefined" ) continue;

		if( sections.length === 1 ) {
			sidebarContent += '<div class="item"><a>' + sections[ 0 ].textContent + '</a></div>';
		} else {
			let subitems = "";
			for( let j = 1; j < sections.length; j ++ ) {
				subitems += `<a class="item" >${sections[ j ].textContent}</a>`;
			}

			sidebarContent += `
				<div class="item">
					<a>${ sections[ 0 ].textContent }</a><i class="dropdown icon"></i>
					<div class="content menu">${ subitems }</div>
				</div>`
			;
		}
	}

	let sidebar = `
		<div class="ui sticky segment">
			<h2 class="ui header">Content</h2>
			<div class="ui vertical following fluid accordion text menu">
				${sidebarContent}
			</div>
		</div>
			`;
	let mobileSidebar = `
		<div class="ui mobile only grid">
			<div class="row">
				<div class="ui segment">
					<h2 class="ui header">Content</h2>
					<div class="ui vertical following fluid accordion menu">
						${sidebarContent}
					</div>
				</div>
			</div>
		</div>`;

	allSections[ 0 ].insertAdjacentHTML( 'beforebegin', mobileSidebar );

	pageContent = document.body.outerHTML + '</div></div></div><div class="four wide computer only four wide tablet only column"><div class="sidebar">' + sidebar + '</div></div></div></div>'
}
