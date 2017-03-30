function loadAsyncBackgrounds() {
	var elements = document.querySelectorAll( "[data-async-background]" );
	for( var i = 0; i < elements.length; i ++ ) {
		var element = elements[ i ];
		loadAsyncBackground( element );
	}
}

function loadAsyncBackground( element ) {
	var imageURL = element.getAttribute( "data-async-background" );

	if( ! imageURL ) return;

	var image = new Image();
	image.onload = function() {
		setBackground( element, imageURL );
	};

	// image.onload( function() {} );
	image.src = imageURL;
}

function setBackground( element, imageURL ) {
	element.style.backgroundImage = "url(" + imageURL + ")";
}

setTimeout( loadAsyncBackgrounds, 0 );