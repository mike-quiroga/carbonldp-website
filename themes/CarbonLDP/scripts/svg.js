const fs = require( "fs" );
const CleanCSS = require( "clean-css" );

// TODO: Handle errors
hexo.extend.helper.register( "svg", ( filePath, className ) => {
	const fileSource = fs.readFileSync( hexo.theme_dir + "source/" + filePath );
	
	let data = `<div class="${className}">${fileSource}</div>`;
	return data;
} );