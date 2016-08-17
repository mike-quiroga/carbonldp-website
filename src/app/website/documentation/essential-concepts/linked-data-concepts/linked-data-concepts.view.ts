import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import "semantic-ui/semantic";

import template from "./linked-data-concepts.view.html!";
import style from "./linked-data-concepts.view.css!text";

@Component( {
	selector: "linked-data-concepts",
	template: template,
	directives: [ CORE_DIRECTIVES ],
	styles: [ style ],
} )
export class LinkedDataConceptsView {
}

export default LinkedDataConceptsView;
