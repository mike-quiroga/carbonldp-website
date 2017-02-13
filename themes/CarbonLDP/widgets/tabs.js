$( document ).ready( function() {
	let tabs = document.querySelectorAll(".tabs-titles > .tabs-title, .tabs-options .tabs-option" );
	for (var i = 0; i < tabs.length; i++) {
		tabs[i].addEventListener('click', changeTabSelection );
	}
});




function changeTabSelection(e) {

	let clickedTab = $(e.currentTarget);
	let selectedOption = clickedTab.attr("data-tab");
	let selectedText = clickedTab.html();
	let tabComponent = clickedTab.parent();

	//select TabComponent to where the clicked tab/option belongs
	while( ! tabComponent.hasClass("tabsComponent")) {
		tabComponent = tabComponent.parent();
	}

	// select TabComponent elements
	let tabs = tabComponent.find(".tabs-title,.tabs-tab")
	let options = tabComponent.find(".tabs-option")

	// Remove all active classes from elements
	options.removeClass("active selected");
	tabs.removeClass("active");

	// Add active classes on selected options/tabs
	tabs.each( function(){
		let tab = $(this);
		if( tab.attr("data-tab") === selectedOption ) tab.addClass("active");
	});
	
	options.each(function() {
		let option = $(this);
		if( option.attr("data-tab") === selectedOption ) option.addClass("active selected");
	});

	// Change text of selection
	if( clickedTab.hasClass("tabs-title")){
		let text =  tabComponent.find(".tabs-options .text");
		text.html(selectedText);
	}

	return true;
}



