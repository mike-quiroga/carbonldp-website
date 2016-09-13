import { Component, ViewEncapsulation } from "@angular/core";

import style from "./documentation.view.css!text";

@Component( {
	selector: "documents",
	template: "<router-outlet></router-outlet>",
	encapsulation: ViewEncapsulation.None,
	styles: [ style ],
} )

export class DocumentationView {

}

export default DocumentationView;
