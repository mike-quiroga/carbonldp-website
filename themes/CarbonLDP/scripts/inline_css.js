const fs = require( "fs" );
const CleanCSS = require( "clean-css" );

// TODO: Handle errors
hexo.extend.helper.register( "inline_css", ( filePath ) => {
	const fileSource = fs.readFileSync( hexo.theme_dir + "source/" + filePath );

	const minifiedSource = new CleanCSS().minify( fileSource ).styles;

	return `<style>${minifiedSource}</style>`;
} );