import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { RouterLink } from "@angular/router-deprecated";

import "semantic-ui/semantic";

import template from "./essential-concepts.view.html!";
import style from "./essential-concepts.view.css!text";

@Component( {
	selector: "essential-concepts",
	template: template,
	styles: [ style ],
	directives: [ CORE_DIRECTIVES, RouterLink ]
} )

export class EssentialConceptsView {
}

export default EssentialConceptsView;
