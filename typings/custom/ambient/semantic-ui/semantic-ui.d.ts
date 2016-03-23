interface SemanticVisibilityArguments {
	once?:boolean;
}

interface SemanticAPISettings {
	// TODO
}

interface SemanticTabSettings {
	// Whether tab should load remote content as same url as history
	auto?:boolean;
	// Whether to record history events for tab changes
	history?:boolean;
	// Do not load content remotely on first tab load. Useful when open tab is rendered on server
	ignoreFirstLoad?:boolean;
	// Whether inline scripts in tab HTML should be parsed on tab load. Defaults to once, parsing only on first load.
	// Can also be set to true or false to always parse or never parse inline scripts.
	evaluateScripts?:string | boolean;
	// Tab should reload content every time it is opened
	alwaysRefresh?:boolean;
	// Tab should cache content after loading locally to avoid server trip on second load
	cache?:boolean;
	// Settings object for $.api call
	apiSettings?:boolean | SemanticAPISettings;
	// Can be set to hash or state. Hash will use an in-page link to create history events. State will use DOM History and load pages from server on refresh.
	historyType?:string;
	// When using historyType state you must specify the base URL for all internal links.
	path?:boolean | string;
	// Tabs are limited to those found inside this context
	context?:boolean | string;
	// If enabled limits tabs to children of passed context
	childrenOnly?:boolean;
	// Maximum amount of nested tabs allowed (avoids recursion)
	maxDepth?:number;

}

interface JQuery {
	visibility:( arguments:SemanticVisibilityArguments ) => JQuery;
	tab:( settings?:SemanticTabSettings ) => JQuery;
	dropdown:( arguments:any ) => JQuery;
	transition:( arguments:any ) => JQuery;
	form:( arguments:any ) => JQuery;
	accordion:( arguments?:any ) => JQuery;
}
