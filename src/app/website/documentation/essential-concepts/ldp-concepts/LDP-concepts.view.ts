import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import $ from "jquery";
import "semantic-ui/semantic";

import template from "./LDP-concepts.view.html!";


@Component( {
	selector: "ldp-concepts",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )
export class LDPConceptsView {
}
export default LDPConceptsView;
