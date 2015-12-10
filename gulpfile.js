var gulp = require( 'gulp' );
var util = require( 'gulp-util' );
var chug = require( 'gulp-chug' );
var watch = require( 'gulp-watch' );

var jspm = require( 'jspm' );

var tslint = require( 'gulp-tslint' );

var sass = require( 'gulp-sass' );
var autoprefixer = require( 'gulp-autoprefixer' );
var sourcemaps = require( 'gulp-sourcemaps' );

var liveServer = require( 'live-server' );

var config = {
	source: {
		typescript: 'src/app/**/*.ts',
		semantic: 'src/semantic/dist/**/*',
		sass: [
			'src/app/**/*.scss',
			'src/assets/**/*.scss'
		]
	}
};

gulp.task( 'ts-lint', function() {
	return gulp.src( config.source.typescript )
		.pipe( tslint() )
		.pipe( tslint.report( 'prose' ) )
	;
});

gulp.task( 'serve', [
    //'build-semantic',
    'compile-styles' ],
    function() {
    /*
	gulp.src( 'src/semantic/gulpfile.js', { read: false } )
		.pipe( chug({
			tasks: [ 'watch' ]
		}) )
	;
	*/

	watch( config.source.sass, function( file ) {
		util.log( 'SCSS file changed: ', file.path );
		gulp.start( 'compile-styles' );
	}).on( 'error', function( error ) {
		util.log( util.colors.red( 'Error' ), error.message );
	});

	return liveServer.start({
		root: 'src',
		open: true,
		file: 'index.html'
	});
});

gulp.task( 'compile-styles', function() {
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

gulp.task( 'build', [ 'build-semantic', 'copy-semantic', 'copy-assets' ], function( done ) {
	return jspm.bundleSFX( 'app/boot.ts', 'dist/main.sfx.js', {
		minify: true,
		mangle: true,
		lowResSourceMaps: false,
		sourceMaps: true
	}).then(
		function() {
			done();
		}, function( error ) {
			done( error );
		}
	);
});

gulp.task( 'build-semantic', function() {
	return gulp.src( 'src/semantic/gulpfile.js', { read: false } )
		.pipe( chug({
			tasks: [ 'build' ]
		}) )
	;
});

gulp.task( 'copy-semantic', [ 'build-semantic' ], function() {
	return gulp.src( 'src/semantic/dist/**/*', {
		base: 'src/semantic/dist'
	}).pipe( gulp.dest( 'dist/assets/semantic' ) );
});

// TODO: Minify files
gulp.task( 'copy-assets', function() {
	return gulp.src( 'src/assets/**/*', {
		base: 'src/assets'
	}).pipe( gulp.dest( 'dist/assets' ) );
});