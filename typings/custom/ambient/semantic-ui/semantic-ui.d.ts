interface SemanticVisibilityArguments {
	once?:boolean;
}

interface SemanticDropdownArguments {
	// TODO
}

interface JQuery {
	visibility:( arguments:SemanticVisibilityArguments ) => JQuery;
	dropdown:( arguments?:SemanticDropdownArguments ) => JQuery;
	transition:( arguments:any ) => JQuery;
	form:( arguments:any ) => JQuery;
	accordion:( arguments?:any ) => JQuery;
}
