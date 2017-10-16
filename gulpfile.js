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

const fs = require( "fs" );
const path = require( "path" );
const matter = require("gray-matter");

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
		"compile:site",
		"minify",
		done
	);
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

gulp.task( "clean:styles", ()=> {
	return del( [ config.dist.styles.pattern ] );
} );

gulp.task( "generate:documentationData", (done)=> {
	fsReadFilePromisify("themes/CarbonLDP/_config.yml")
		.then( (hexoConfig) => {
			
			hexoVersionsConfig = hexoConfig.split("# Versions")[1].split("\n#")[0].split("-").slice(1);
			
			hexoVersionsConfig.forEach(
				( version, i ) =>{
					version = '{"'+version.replace(/:/g,'":"').replace(/\n/g,'","').split(" ").join("")+'}';
					version = version.replace(',"}',"}");
					version = JSON.parse(version);
				
					hexoVersionsConfig[i] = version;
				});
			
			let versions = hexoVersionsConfig;
			
			recursiveReaddir("source/documentation")
				.then( ( versionsDocumentation ) => {
					versionsDocumentation.forEach((versionDocumentation,i)=>{

						function findLabel(version){
							return version.version === versionDocumentation.name;
						}
						
						versionsDocumentation[i].label = versions.find(findLabel).label;
						
					});

					
					return versionsDocumentation;
				}).then( (versionsDocumentation) => {
					versionsDocumentation = {versions: versionsDocumentation };
					fs.writeFile("source/_data/documentation.json", JSON.stringify(versionsDocumentation, null, 4)), (error) => {
						if( err ) throw err;
						console.log("Documentation data created");
						done();
					};
				
			});
		});
} );


function recursiveReaddir(source, level=0){
	return fsReaddirPromisify(source).then((files) => {
		let promises=[];
		let directory=[];
		files.forEach((file,i) => {
			promises.push(
				fsStatPromisify(path.join(source,file))
					.then( (stats) => {
							if(stats.isDirectory()) {
								let nextSource = path.join(source, file);
								return recursiveReaddir(nextSource, level+1)
									.then((results) => {
										let readingFiles = [];
										
										let frontMatter = fsReadFilePromisify(nextSource+"/index.ejs").then(
											(fileContent) => {
												let frontMatterJSON = matter(fileContent).data;
												return frontMatterJSON;
											}
										).catch( function(err) {
											 fsReadFilePromisify( nextSource + "/index.md" )
												 .then( ( fileContent ) => {
													 let frontMatterJSON = matter(fileContent).data;
													 return frontMatterJSON;
												 } ).catch( function() {
													 console.log( "NotFound: " + nextSource + "/index.md, it may exist as .ejs" );
												 } );
										 });
										readingFiles.push(frontMatter);

										return Promise.all(readingFiles).then((frontMatter)=>{ 
											
												switch( level ) {
													case 0:
														name = "name";
														subdirectoryName = "categories";
														break;
													default:
														name = "link";
														subdirectoryName = "documents";
														break;
												}
											
												directory.push( Object.assign({ [name]: file, [subdirectoryName]: results}, frontMatter[0] ) );
											
										 });
									});
							}
						}));
			
		});

		return Promise.all(promises).then(()=>{ if(directory.length > 0) return directory;});
		
	});
	

};


// unnecessary in node 8.
function fsReadFilePromisify(filename){
	return new Promise(
		function (resolve, reject){
			fs.readFile(filename, {encoding: "utf8"} ,
				(error, fileContent)=>{
					if(error){
						reject(error);
					} else {
						resolve(fileContent);
					}
				});
		});
}

function fsStatPromisify(source){
	return new Promise(
		function (resolve, reject) {
			fs.stat(source,
				(error, stats) => {
					if (error) {
						reject(error);
					} else {
						resolve(stats);
					}
				});
		});	
}

function fsReaddirPromisify(source){
	return new Promise(
		function (resolve, reject) {
			fs.readdir(source,
				(error, files) => {
					if (error) {
						reject(error);
					} else {
						resolve(files);
					}
				});
		});
}
