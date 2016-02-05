const gulp = require( 'gulp' );
const util = require( 'gulp-util' );
const chug = require( 'gulp-chug' );
const watch = require( 'gulp-watch' );

const jspm = require( 'jspm' );

const tslint = require( 'gulp-tslint' );

const sass = require( 'gulp-sass' );
const autoprefixer = require( 'gulp-autoprefixer' );
const sourcemaps = require( 'gulp-sourcemaps' );

const liveServer = require( 'live-server' );

const config = {
	source: {
		typescript: 'src/app/**/*.ts',
		semantic: 'src/semantic/dist/**/*',
		sass: [
			'src/app/**/*.scss',
			'src/assets/**/*.scss'
		]
	},
	nodeDependencies: [
		'node_modules/es6-shim/es6-shim.js',
		'node_modules/systemjs/dist/system-polyfills.js',
		'node_modules/angular2/bundles/angular2-polyfills.js',
		'node_modules/systemjs/dist/system.js',
		'node_modules/rxjs/bundles/Rx.js',
	]
};

gulp.task( 'ts-lint', () => {
	return gulp.src( config.source.typescript )
		.pipe( tslint() )
		.pipe( tslint.report( 'prose' ) )
		;
});

gulp.task( 'serve', [ 'build-semantic', 'compile-styles' ], () => {
	gulp.src( 'src/semantic/gulpfile.js', { read: false } )
		.pipe( chug({
			tasks: [ 'watch' ]
		}) )
	;

	watch( config.source.sass, ( file ) => {
		util.log( 'SCSS file changed: ', file.path );
		gulp.start( 'compile-styles' );
	}).on( 'error', function( error ) {
		util.log( util.colors.red( 'Error' ), error.message );
	});

	return liveServer.start({
		root: 'src',
		open: true,
		file: 'index.html',
		ignore: '/docs/getting-started-with-the-rest-api.html'
	});
});

gulp.task( 'compile-styles', () => {
	return gulp.src( config.source.sass, { base: './'} )
		.pipe( sourcemaps.init() )
		.pipe( sass().on( 'error', sass.logError ) )
		.pipe( autoprefixer({
			browsers: [ 'last 2 versions' ]
		}) )
		.pipe( sourcemaps.write( '.' ) )
		.pipe( gulp.dest( '.' ) )
		;
});

gulp.task( 'build', [ 'build-semantic', 'compile-styles', 'copy-semantic', 'copy-assets' ], () => {
	return jspm.bundleSFX( 'app/boot.ts', 'dist/site/main.sfx.js', {
		minify: true,
		mangle: false,
		lowResSourceMaps: false,
		sourceMaps: true
	});
});

gulp.task( 'build-semantic', () => {
	return gulp.src( 'src/semantic/gulpfile.js', { read: false } )
		.pipe( chug({
			tasks: [ 'build' ]
		}) )
		;
});

gulp.task( 'copy-semantic', [ 'build-semantic' ], () => {
	return gulp.src( 'src/semantic/dist/**/*', {
		base: 'src/semantic/dist'
	}).pipe( gulp.dest( 'dist/site/assets/semantic' ) );
});

// TODO: Minify files
gulp.task( 'copy-assets', () => {
	return gulp.src( 'src/assets/**/*', {
		base: 'src/assets'
	}).pipe( gulp.dest( 'dist/site/assets' ) );
});

gulp.task( 'copy-node-dependencies', () => {
	return gulp.src( config.nodeDependencies ).pipe( gulp.dest( 'src/assets/node_modules' ) );
});