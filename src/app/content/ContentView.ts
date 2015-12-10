import { Component, CORE_DIRECTIVES } from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';
import ContentService from 'app/content/ContentService';

import template from './template.html!';

@Component({
    // Selector matches the route alias?
    selector: 'docs',
    template: template,
    directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES ]
})
export default class ContentView {

    static parameters = [ [ Router ], [ ContentService ], [RouteParams] ];

    router:Router;
    contentService:ContentService;
    routeParams: RouteParams;

    constructor( router:Router, contentService:ContentService, routeParams:RouteParams ) {

        console.log(">> ContentView -> constructed");

        this.router = router;
        this.contentService = contentService;
        this.routeParams = routeParams;

        let id = this.routeParams.get('id');

        console.log("-- ContentView -> Got id: " + id);

        let document = this.contentService.getDocumentById(id).then(
            ( doc ) => {
                console.log("-- ContentView -> document: " + doc);
            }
        ).catch( console.error );

    }

}