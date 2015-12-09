import { Component, CORE_DIRECTIVES } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction } from 'angular2/router';
import ContentService from 'app/content/ContentService';

import template from './template.html!';

@Component({
    // Selector matches the route alias?
    selector: 'docs',
    template: template,
    directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
})
export default class ContentView {

    static parameters = [ [ Router ], [ ContentService ] ];

    router:Router;
    contentService:ContentService;

    constructor( router:Router, contentService:ContentService ) {

        console.log(">> ContentView -> constructed");

        this.router = router;
        this.contentService = contentService;

    }

}