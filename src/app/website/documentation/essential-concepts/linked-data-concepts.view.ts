import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./linked-data-concepts.view.html!";
import style from "./linked-data-concepts.view.css!text";

@Component( {
	selector: "linked-data-concepts",
	template: template,
	styles: [ style ],
} )
export class LinkedDataConceptsView {
}

export default LinkedDataConceptsView;
