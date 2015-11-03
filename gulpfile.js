var gulp = require( 'gulp' );
var gutil = require( 'gulp-util' );
var chug = require( 'gulp-chug' );

var jspm = require( 'jspm' );

var tslint = require( 'gulp-tslint' );

var autoprefixer = require( 'gulp-autoprefixer' );
var sourcemaps = require( 'gulp-sourcemaps' );

var liveServer = require( 'live-server' );

var config = {
	source: {
		typescript: 'src/app/**/*.ts',
		semantic: 'src/semantic/dist/**/*'
	}
};

gulp.task( 'ts-lint', function() {
	return gulp.src( config.source.typescript )
		.pipe( tslint() )
		.pipe( tslint.report( 'prose' ) )
	;
});

gulp.task( 'serve', [ 'build-semantic' ], function( done ) {
	gulp.src( 'src/semantic/gulpfile.js', { read: false } )
		.pipe( chug({
			tasks: [ 'watch' ]
		}) )
	;

	return liveServer.start({
		root: 'src',
		open: true,
		file: 'index.html'
	});
});

gulp.task( 'build', [ 'build-semantic', 'copy-semantic' ], function( done ) {
	return jspm.bundleSFX( 'app/boot.ts', 'dist/main.sfx.js', {
		minify: true,
		mangle: true,
		lowResSourceMaps: true,
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