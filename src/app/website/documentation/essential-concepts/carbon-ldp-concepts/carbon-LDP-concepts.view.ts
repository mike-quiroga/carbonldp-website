import { Component, ElementRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import $ from "jquery";
import "semantic-ui/semantic";

import template from "./carbon-LDP-concepts.view.html!";

@Component( {
	selector: "carbon-ldp-concepts",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )
export class CarbonLDPConceptsView {
}

export default CarbonLDPConceptsView;
