(function( $ ) {

	// header js
	$( ".navMenu-dropdownButton" ).dropdown( {
		on: "hover",
		action: "hide",
	} );

	$( ".navMenu-dropdownButton--mobile" ).dropdown();

	var verticalMenu = $( ".navMenu-verticalMenu" );

	$( ".navMenu-openerButton" ).on( "click", function( e ) {
		e.preventDefault();
		verticalMenu.toggle();
	} );

	//documentation-home dropdown versions button
	$( ".versionsMenu-dropdown" ).dropdown();

	//individual document versionsMenu sticky
	$( ".ui.sticky.sticky-wrapper" ).sticky( {
		observeChanges: true,
		context: ".mainContent",
		offset: 0,
		bottomOffset: 16
	} );
	// Activates scroll with offset
	$( ".sidebar .menu a[href], .categoriesMenu-button, .staticContentMenu .menu a[href]" ).on( "click", scrollTo );

	// Scroll to selected section or subsection in the article
	function scrollTo( event ) {
		var id = $( event.currentTarget ).attr( "href" ).replace( "#", "" );
		var $element = $( "#" + id );
		var position = $element.offset().top - 100;

		$element.addClass( "active" );

		$( "html, body" ).animate( {
			scrollTop: position
		}, 500 );
		location.hash = "#" + id;
		event.preventDefault();

		return false;
	}

	function randomToken() {
		var tokenElements = document.querySelectorAll( ".js-generateRandomToken" )

		for( var i = 0, length = tokenElements.length; i < length; ++ i ) {
			var randomToken = randomString( 32, "aA#" );
			tokenElements[ i ].innerHTML = randomToken;
		}

		return true;
	}

	function randomString( length, chars ) {

		var mask = "";
		if( chars.indexOf( "a" ) > - 1 ) mask += "abcdefghijklmnopqrstuvwxyz";
		if( chars.indexOf( "A" ) > - 1 ) mask += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if( chars.indexOf( "#" ) > - 1 ) mask += "0123456789";
		if( chars.indexOf( "!" ) > - 1 ) mask += "~`!@#$%^&*()_+-={}[]:\";'<>?,./|\\";

		var result = "";
		for( var i = length; i > 0; -- i ) result += mask[ Math.floor( Math.random() * mask.length ) ];

		return result;
	};


	// activate accordions
	$( ".ui.accordion" ).accordion();

	var scrollTopButton = document.querySelector( ".scrollTop-button" );

	scrollTopButton.addEventListener( "click", scrollTop )
	window.addEventListener( 'scroll', windowScrolling );

	function windowScrolling() {
		if( document.body.scrollTop === 0 ) {
			scrollTopButton.style.display = "none";
		} else {
			scrollTopButton.style.display = "block";
		}
	}

	function scrollTop() {
		window.scroll( 0, 0 );
	}


	randomToken();

})( jQuery );
