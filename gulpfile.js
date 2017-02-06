const  gulp = require( "gulp");

const util = require( "gulp-util" );
const sass = require( "gulp-sass" );
const del = require( "del");
const minifycss = require( "gulp-minify-css" );
const autoprefixer = require( "gulp-autoprefixer" );
const uglify = require( "gulp-uglify" );
const rename = require( "gulp-rename" );
const sourcemaps = require( "gulp-sourcemaps" );

const config = {
	source: {
		sass: [
			"themes/CarbonLDP/source/assets/styles/_scss/*.scss"
		]
	}
};

gulp.task( "default", [ "compile:styles" ] );

gulp.task( "compile:styles", function() {
	return gulp.src( config.source.sass )
		.pipe( sourcemaps.init() )
		.pipe( sass({ style: 'expanded' }))
		.pipe( sourcemaps.write() )
		.pipe( autoprefixer("last 2 versions"))
		.pipe( gulp.dest("themes/CarbonLDP/source/assets/styles/css"))
		.pipe( sourcemaps.init({loadMaps: true}))
		.pipe( rename({suffix: '.min'}))
		.pipe( minifycss())
		.pipe( sourcemaps.write())
		.pipe( gulp.dest('themes/CarbonLDP/source/assets/styles/css'));
});
