declare module "highlight.js" {
	interface HighlightResult {
		language?:string;
		relevance:number;
		highlightedCode:string;
		second_best?:HighlightResult;
	}

	interface HighlightConfiguration {
		classPrefix?:strig;
		tabReplace?:boolean;
		useBR?:boolean;
		languages?:string[];
	}

	let highlight:{
		highlight:( languageName:string, codeToHighlight:string, ignoreIllegals?:boolean, continuation?:any ) => HighlightResult,
		/**
		 * Highlights code with language auto detection
		 * @param codeToHighlight
		 * @param languageSubset
		 */
		highlightAuto:( codeToHighlight:string, languageSubset?:any ) => HighlightResult,
		fixMarkup:( codeToHighlight:string ) => string,
		highlightBlock:( node:Node ) => void,
		configure:( options:HighlightConfiguration ) => void,

		initHighlighting:() => void,
		initHighlightingOnLoad:() => void,
		registerLanguage:() => void,
		listLanguages:() => void,
		getLanguage:() => void,
		inherit:() => void,
	};
	export default highlight;
}
