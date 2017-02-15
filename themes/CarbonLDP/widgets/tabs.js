$( document ).ready( function() {
	var tabs = document.querySelectorAll( ".tabs-titles > .tabs-title, .tabs-options .tabs-option" );
	for( var i = 0; i < tabs.length; i ++ ) {
		tabs[ i ].addEventListener( 'click', changeTabSelection );
	}
} );

function changeTabSelection( e ) {

	var clickedTab = $( e.currentTarget );
	var selectedOption = clickedTab.attr( "data-tab" );
	var selectedText = clickedTab.html();
	var tabComponent = clickedTab.parent();

	//select TabComponent to where the clicked tab/option belongs
	while( ! tabComponent.hasClass( "tabsComponent" ) ) {
		tabComponent = tabComponent.parent();
	}

	// select TabComponent elements
	var tabs = tabComponent.find( ".tabs-title,.tabs-tab" );
	var options = tabComponent.find( ".tabs-option" );

	// Remove all active classes from elements
	options.removeClass( "active selected" );
	tabs.removeClass( "active" );

	// Add active classes on selected options/tabs
	tabs.each( function() {
		var tab = $( this );
		if( tab.attr( "data-tab" ) === selectedOption ) tab.addClass( "active" );
	} );

	options.each( function() {
		var option = $( this );
		if( option.attr( "data-tab" ) === selectedOption ) option.addClass( "active selected" );
	} );

	// Change text of selection
	if( clickedTab.hasClass( "tabs-title" ) ) {
		var text = tabComponent.find( ".tabs-options .text" );
		text.html( selectedText );
	}

	return true;
}



