hexo.extend.helper.register( "breadcrumb", ( page ) => {
	let breadcrumbElements = {};
	let path = page.path.replace("/index.html", "");
	let slugs = path.split( "/" );

	let lastSlug = slugs[slugs.length-1];

	if( lastSlug === "")
			slugs.splice( lastSlug, 1 );


	breadcrumbElements.titles = getTitles(slugs);
	breadcrumbElements.paths = getPaths(slugs);

	return breadcrumbElements;
});

function getTitles( slugs ){
	let titles = [];

	slugs.forEach( ( slug, i ) => {
		let title = slug.split("-");

		title.forEach( ( word, j ) =>{
			if( word === "javascript" ){
				title[j] = "JavaScript";

			} else {

				if( word === "rest" || word === "api" || word === "sdk" || word === "rdf" ) {
					title[ j ] = word.toUpperCase();
				} else {
					title[j] = word.charAt(0).toUpperCase()+ word.substr(1).toLowerCase();
				}
			}
		});

		title = title.join(" ")
		titles.push(title);
	});


	return titles;
}

function getPaths( slugs ){
	let paths = [];
	slugs.forEach( ( slug, i ) => {

		let auxi = 0
		let breadcrumbPath = "";

		while( auxi <= i ) {
			breadcrumbPath += "/" + slugs[ auxi ];
			auxi ++;
		}

		paths.push(breadcrumbPath);
	});

	return paths;
}