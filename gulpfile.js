var gulp = require( 'gulp' );
var gutil = require( 'gulp-util' );

var jspm = require( 'jspm' );

var tslint = require( 'gulp-tslint' );

var autoprefixer = require( 'gulp-autoprefixer' );
var sourcemaps = require( 'gulp-sourcemaps' );

var liveServer = require( 'live-server' );

var config = {
	source: {
		typescript: 'src/app/**/*.ts'
	}
};

gulp.task( 'ts-lint', function() {
	return gulp.src( config.source.typescript )
		.pipe( tslint() )
		.pipe( tslint.report( 'prose' ) )
	;
});

gulp.task( 'serve', function( done ) {
	liveServer.start({
		root: 'src',
		open: true,
		file: 'index.html'
	});
});

gulp.task( 'build', function( done ) {
	jspm.bundleSFX( 'app/boot.ts', 'dist/main.sfx.js', {
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