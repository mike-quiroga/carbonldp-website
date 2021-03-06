$( document ).ready( function() {
	var $sidebar = $( ".staticContentMenu" );
	var $content = $( "." + $sidebar.attr( "content" ) );
	var $sections = $content.children( ".mainContent-section" );

	// Detect when section is passed
	$sections.visibility( {
		once: false,
		offset: 150,
		onTopPassed: function() {
			// _self.activateSection( this );
			console.log( "top passed" );
		},
		onTopPassedReverse: function() {
			// _self.deactivateFirstSection( this );
		},
		onBottomPassedReverse: function() {
			// _self.activateSection( this );
		},
	} );

	// Activate toggle dropdown
	$( ".staticContentMenu .item > .dropdown.icon" ).on( "click", toggleDropdown );

	// Toggle selected accordion menu in sidebar
	function toggleDropdown( event ) {
		var $target = $( event.currentTarget );
		$( ".ui.sticky" ).sticky( "refresh" );
		var $accordion = $target.parent( ".item" ).find( ".content.menu" );
		if( $accordion ) {
			var accordionIsActive = $accordion.hasClass( "active" );

			if( accordionIsActive ) {
				$accordion.removeClass( "active" );
				$accordion.removeClass( "toggled" );
			} else {
				$accordion.addClass( "active" );
				$accordion.addClass( "toggled" );
			}
		}

		return false;
	}
} );