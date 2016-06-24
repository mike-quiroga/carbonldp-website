/// <reference no-default-lib="true"/>
/// <reference path="./../node_modules/typescript/lib/lib.es6.d.ts" />

/// <reference path="./index.d.ts" />

/// <reference path="./custom/highlightjs/index.d.ts" />
/// <reference path="./custom/semantic-ui/semantic-ui.d.ts" />

declare module "*.html!" {
	let value:string;
	export default value;
}

declare module "*.css!" {
	let value:any;
	export default value;
}

declare module "*!text" {
	let value:string;
	export default value;
}
