interface SemanticVisibilityArguments {
	once?:boolean;
}

interface JQuery {
	visibility:( arguments:SemanticVisibilityArguments ) => JQuery;
	dropdown:( arguments:any ) => JQuery;
	transition:( arguments:any ) => JQuery;
	form:( arguments:any ) => JQuery;
}