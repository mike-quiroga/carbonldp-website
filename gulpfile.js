const del = require( "del" );

const gulp = require( "gulp" );
const runSequence = require( "run-sequence" );

const Hexo = require( "hexo" );

const sass = require( "gulp-sass" );
const autoprefixer = require( "gulp-autoprefixer" );
const replace = require( 'gulp-replace' );

const htmlMinifier = require( "gulp-htmlmin" );
const stylesMinifier = require( "gulp-clean-css" );
const scriptsMinifier = require( "gulp-uglify" );

const config = {
	source: {
		assets: {
			semantic: {
				components: {}
			},
		},
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
config.source.assets.semantic.dir = config.source.assets.dir + "semantic/";
config.source.assets.semantic.components.dir = config.source.assets.semantic.dir + "components/";
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
		[ "compile:styles", "include:semantic" ],
		"replace:import-font",
		"clean:site",
		"compile:site",
		"minify",
		done
	);
} );

gulp.task( "clean:site", () => {
	return del( config.dist.dir );
} );

gulp.task( "clean:styles", () => {
	return del( [ config.dist.styles.pattern ] );
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

gulp.task( "copy:semantic", () => {
	return gulp.src( [ "./node_modules/semantic-ui/dist/**/*" ] )
		.pipe( gulp.dest( config.source.assets.dir + "semantic/" ) );
} );

gulp.task( "include:semantic", ( done ) => {
	runSequence(
		"copy:semantic",
		"replace:import-font",
		"replace:font-family",
		done
	);
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

gulp.task( "replace:import-font", () => {
	return gulp.src(
		[
			config.source.assets.semantic.dir + "semantic.css",
			config.source.assets.semantic.dir + "semantic.min.css",
			config.source.assets.semantic.components.dir + "site.css",
			config.source.assets.semantic.components.dir + "site.min.css"
		], { base: './' } )
		.pipe( replace( "@import url('https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic&subset=latin');", "" ) )
		.pipe( replace( "@import url(https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic&subset=latin);", "" ) )
		.pipe( gulp.dest( "./" ) );
} );

gulp.task( "replace:font-family", () => {
	return gulp.src(
		[
			config.source.assets.semantic.dir + "semantic.css",
			config.source.assets.semantic.dir + "semantic.min.css",
			config.source.assets.semantic.components.dir + "site.css",
			config.source.assets.semantic.components.dir + "site.min.css",
			config.source.assets.semantic.components.dir + "**/*.css",
			config.source.assets.semantic.components.dir + "**/*.min.css"
		], { base: './' } )
		.pipe( replace( "'Lato', 'Helvetica Neue', Arial, Helvetica, sans-serif", "Helvetica, Arial, sans-serif" ) )
		.pipe( replace( "Lato,'Helvetica Neue',Arial,Helvetica,sans-serif", "Helvetica,Arial,sans-serif" ) )
		.pipe( gulp.dest( "./" ) );
} );
