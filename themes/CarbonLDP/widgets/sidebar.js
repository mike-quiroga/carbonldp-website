$( document ).ready( function() {

	var $sidebar = $( ".sidebar" );
	var $sidebarFollowingMenu = $( ".sidebar" ).find( ".following.menu" );
	var $content = $( "." + $sidebar.attr( "content" ) );
	var $sections = $content.children( "section" );
	var $subSections = $sections.children( "section" );

	// Detect when section is passed
	$sections.visibility( {
		once: false,
		offset: 100,
		onTopPassed: function() {
			activateSection( this, $sidebarFollowingMenu, $sections );
		},
		onTopPassedReverse: function() {
			deactivateFirstSection( this, $sidebarFollowingMenu, $sections );
		},
		onBottomPassedReverse: function() {
			activateSection( this, $sidebarFollowingMenu, $sections );
		},
	} );

	$subSections.visibility( {
		once: false,
		offset: 100,
		onTopPassed: function() {
			activateSubSection( this, $sidebarFollowingMenu, $subSections );
		},
		onBottomPassedReverse: function() {
			activateSubSection( this, $sidebarFollowingMenu, $subSections );
		}
	} );


	// Activate toggle dropdown
	$( ".sidebar .item > .dropdown.icon" ).on( "click", toggleDropdown );

	//Activate click on items and subitems
	$( ".sidebar .item > .title" ).on( "click", activateItem );
	$( ".sidebar  a.item " ).on( "click", activateSubItem );

	// Toggle selected accordion menu in sidebar
	function toggleDropdown( event ) {
		var $target = $( event.currentTarget );

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

	function activateItem( event ) {

		var $item = $( event.currentTarget ).parent( ".item" );

		var $allItems = $item.siblings( ".item" );
		$allItems.removeClass( "active" );
		$item.addClass( "active" );

		event.preventDefault();

		return false;

	}

	function activateSubItem( event ) {

		var $item = $( event.currentTarget );

		var $allItems = $item.siblings( ".item" );
		$allItems.removeClass( "active" );
		$item.addClass( "active" );

		event.preventDefault();

		return false;

	}


	function activateSection( elm, $menu, $sections ) {

		var $section = $( elm );
		var index = $sections.index( $section );
		var $followSection = $menu.children( ".item" );
		var $currentSection = $followSection.eq( index );

		var isActive = $currentSection.hasClass( "active" );

		var hasSubsection = $sections.eq( index ).children( "section" ).length > 0;

		$followSection.removeClass( "active" );
		$followSection.find( ".active" ).not( ".toggled" ).removeClass( "active" );

		if( ! isActive ) {

			$currentSection.addClass( "active" );
		}

		if( hasSubsection ) {
			$currentSection.find( ".menu" ).addClass( "active" );
		}

		return false;

	}

	function activateSubSection( elm, $menu, $subSections ) {
		var $section = $( elm );
		var index = $subSections.index( $section );
		var $followSection = $menu.find( ".menu > .item" );
		var $activeSection = $followSection.eq( index );
		var isActive = $activeSection.hasClass( "active" );
		var $subSectionMenu = $menu.children( ".item" ).find( ".menu" );
		var subSectionMenuIsActive = $subSectionMenu.hasClass( "active" );

		if( index !== - 1 && ! isActive ) {
			$followSection.filter( ".active" ).removeClass( "active" );
			$activeSection.addClass( "active" );
		}

		if( ! subSectionMenuIsActive ) {
			$subSectionMenu.addClass( "active" );
		}

		return false;
	}

	function deactivateFirstSection( elm, $menu, $sections ) {

		var $section = $( elm );
		var index = $sections.index( $section );
		var $followSection = $menu.children( ".item" );
		var $currentSection = $followSection.eq( index );
		var isActive = $currentSection.hasClass( "active" );
		var $subSectionMenu = $currentSection.find( ".menu" );


		if( index === 0 ) {

			$currentSection.removeClass( "active" );

			if( $subSectionMenu.length > 0 ) {
				$currentSection.find( ".menu" ).not( ".toggled" ).removeClass( "active" );
				$subSectionMenu.find( ".item" ).removeClass( "active" );
			}

		}
		
		return false;
	}


} );