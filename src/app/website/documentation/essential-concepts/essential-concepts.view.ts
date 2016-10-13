import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./essential-concepts.view.html!";
import style from "./essential-concepts.view.css!text";

@Component( {
	selector: "essential-concepts",
	template: template,
	styles: [ style ],
} )

export class EssentialConceptsView {
}

export default EssentialConceptsView;
