const fs = require( "fs" );
const CleanCSS = require( "clean-css" );

// TODO: Handle errors
hexo.extend.helper.register( "svg", ( filePath, className ) => {
	const fileSource = fs.readFileSync( hexo.theme_dir + "source/" + filePath );
	
	let data = `<svg-wrapper class="${className}">${fileSource}</svg-wrapper>`;
	return data;
} );