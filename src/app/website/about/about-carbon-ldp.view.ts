import { Component } from "@angular/core";

import "semantic-ui/semantic";

import template from "./about-carbon-ldp.view.html!";
import style from "./about-carbon-ldp.view.css!text";

@Component( {
	selector: "about-carbon-ldp",
	template: template,
	styles: [ style ],
} )

export class AboutCarbonLDPView {
}

export default AboutCarbonLDPView;