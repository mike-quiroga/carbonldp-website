( function(){
	var queryString = { completeQueryString : document.location.search.replace( "?","" ),
						parameters: []};
	var allParameters  = queryString["completeQueryString"].split("&")

	for (var i = 0; i < allParameters.length; i++){
		allParameters[i] = allParameters[i].split("=");
		queryString.parameters.push({"key" : allParameters[i][0], "value": allParameters[i][1]});
	}


	var registered = false;
	var successMessage = document.querySelector(".registrationSuccessMessage");
	var parameters = queryString["parameters"];

	for( var i = 0; i < queryString.parameters.length; i++ ){
		if( parameters[0].key === "registered" ){
			successMessage.classList.add("success");

			successMessage.querySelector(".successMessage").style.display = "block";
		}
	}


})();