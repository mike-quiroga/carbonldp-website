const jspm = require( "jspm" );

jspm.import( __dirname + "/boot.js" ).then( () => {
	return jspm.import( "server/boot" );
} ).catch( console.error );