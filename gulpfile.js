const del = require( "del" );

const gulp = require( "gulp" );
const runSequence = require( "run-sequence" );
const util = require( "gulp-util" );
const chug = require( "gulp-chug" );

const Hexo = require( "hexo" );

const sass = require( "gulp-sass" );
const autoprefixer = require( "gulp-autoprefixer" );

const htmlMinifier = require( "gulp-htmlmin" );
const stylesMinifier = require( "gulp-clean-css" );
const scriptsMinifier = require( "gulp-uglify" );

const config = {
	source: {
		assets: {},
		styles: {
			sass: {},
			css: {}
		},
		semantic: {},
	},
	dist: {
		html: {},
		styles: {},
		scripts: {},
	},
};

config.source.dir = "themes/CarbonLDP/source/";
config.source.assets.dir = config.source.dir + "assets/";
config.source.styles.dir = config.source.assets.dir + "styles/";
config.source.styles.css.dir = config.source.styles.dir + "css/";
config.source.styles.sass.dir = config.source.styles.dir + "_scss/";
config.source.styles.sass.pattern = config.source.styles.sass.dir + "*.scss";
config.source.semantic.dir = "themes/CarbonLDP/semantic/";
config.source.semantic.gulpfile = config.source.semantic.dir + "gulpfile.js";

config.dist.dir = "public/";
config.dist.html.pattern = config.dist.dir + "**/*.html";
config.dist.html.minify = {
	collapseWhitespace: true,
	caseSensitive: true,
	minifyCSS: true,
	minifyJS: true,
	removeComments: true,
};
config.dist.styles.dir = config.dist.dir + "assets/styles/";
config.dist.styles.pattern = config.dist.styles.dir + "**/*.css";
config.dist.styles.minify = {};
config.dist.scripts.dir = config.dist.dir + "assets/scripts/";
config.dist.scripts.pattern = config.dist.scripts.dir + "**/*.js";
config.dist.scripts.minify = {};


gulp.task( "default", [ "build" ] );

gulp.task( "build", ( done ) => {
	runSequence(
		[ "compile:styles", "compile:semantic" ],
		"clean:site",
		"compile:site",
		"minify",
		done
	);
} );

gulp.task( "clean:site", () => {
	return del( config.dist.dir );
} );

gulp.task( "compile:semantic", () => {
	return gulp.src( config.source.semantic.gulpfile, { read: false } )
		.pipe( chug( {
			tasks: [ "build" ]
		} ) )
		;
} );

gulp.task( "compile:site", ( done ) => {
	const hexo = new Hexo( process.cwd(), {} );

	Promise.resolve().then( () => {
		return hexo.init();
	} ).then( () => {
		return hexo.call( "generate", {} );
	} ).then( () => {
		done();
	} ).catch( ( error ) => {
		done( error );
	} );
} );

gulp.task( "compile:styles", function() {
	return gulp.src( config.source.styles.sass.pattern )
		.pipe( sass( { style: "expanded" } ) )
		.pipe( gulp.dest( config.source.styles.css.dir ) )
} );

gulp.task( "minify", [ "minify:html", "minify:styles", "minify:scripts" ] );

gulp.task( "minify:html", () => {
	return gulp.src( config.dist.html.pattern )
		.pipe( htmlMinifier( config.dist.html.minify ) )
		.pipe( gulp.dest( config.dist.dir ) );
} );

gulp.task( "minify:styles", () => {
	return gulp.src( config.dist.styles.pattern )
		.pipe( autoprefixer( "last 2 versions" ) )
		.pipe( stylesMinifier( config.dist.styles.minify ) )
		.pipe( gulp.dest( config.dist.styles.dir ) );
} );

gulp.task( "minify:scripts", () => {
	return gulp.src( config.dist.scripts.pattern )
		.pipe( scriptsMinifier( config.dist.scripts.minify ) )
		.pipe( gulp.dest( config.dist.scripts.dir ) );
} );

gulp.task( "clean:styles", () => {
	return del( [ config.dist.styles.pattern ] );
} );