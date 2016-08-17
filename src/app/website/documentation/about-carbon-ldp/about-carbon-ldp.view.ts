import { Component } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";

import "semantic-ui/semantic";

import template from "./about-carbon-LDP.view.html!";

@Component( {
	selector: "about-carbon-ldp",
	template: template,
	directives: [ CORE_DIRECTIVES ],
} )

export class AboutCarbonLDPView {
}

export default AboutCarbonLDPView;