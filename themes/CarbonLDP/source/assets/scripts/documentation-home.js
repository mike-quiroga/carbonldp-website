(function() {
	var queryString = {
		completeQueryString: document.location.search.replace( "?", "" ),
		parameters: []
	};
	var allParameters = queryString[ "completeQueryString" ].split( "&" );

	for( var i = 0; i < allParameters.length; i ++ ) {
		allParameters[ i ] = allParameters[ i ].split( "=" );
		queryString.parameters.push( { "key": allParameters[ i ][ 0 ], "value": allParameters[ i ][ 1 ] } );
	}

	var documentTitle, documentVersion;
	var doesntExistMessage = document.querySelector( ".documentDoesntExistMessage" );
	var parameters = queryString[ "parameters" ];

	for( var j = 0; j < queryString.parameters.length; j ++ ) {

		if( parameters[ j ].key === "document" ) documentTitle = parameters[j].value;
		if(parameters[j].key === "version") documentVersion = parameters[j].value;
	}

	var documentDoesntExistMessage = "The document: <b>"+documentTitle+"</b> doesn't exist for version: <b>"+documentVersion+"</b>. Here is a list of all documents available in version "+documentVersion;

	for( var j = 0; j < queryString.parameters.length; j ++ ) {
		if( parameters[ j ].key !== "exists" ) continue;
			if( parameters[j].value === "false" ) {

				doesntExistMessage.querySelector("p").innerHTML = documentDoesntExistMessage;
				doesntExistMessage.classList.add( "notFound" );
				document.querySelector( ".notFound" ).style.display = "block";
				break;
		}
	}

	$('.message.documentDoesntExistMessage .close')
		.on('click', function() {
			$(this)
				.closest('.message')
				.transition('fade')
			;
		})
	;

})();