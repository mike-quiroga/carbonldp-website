module.exports = {
	selector: "sidebar",
	templateURL: "sidebar.ejs",
	preRender: ( element, data, document, _ ) => {
		// Read attribute "content-selector"
		// querySelector to load content
		// Process content to generate sidebar
		// let contentSelector = element.getAttribute("content");
		//
		// if ( contentSelector === "null") return;
		// var content = _.$("."+contentSelector);
		//
		// var contentFirstLevelChildren = content.children;
		//
		// data.sections = buildSectionsTree( contentFirstLevelChildren );
		//
		// data.sections = [];
		//
		//
		//
		// data.sections = content.outerHTML;
		return Promise.resolve();
	}
};


function buildSectionsTree( list, parent){
	var sectionsTree = [];
	for ( var i = 0, listLength = list.length; i < listLength; i++ ){

		if ( ! list[i].classList.contains("mainContent-section") ) continue;
			var section = list[i].querySelector(".ui.header").textContent;
			sectionsTree.push( section );
	}

	return sectionsTree;
}