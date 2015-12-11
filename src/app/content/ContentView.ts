import { Component, CORE_DIRECTIVES, DynamicComponentLoader, ElementRef, View} from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';
import ContentService from 'app/content/ContentService';

import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import template from './template.html!';

@Component({
    // Selector matches the route alias?
    selector: 'compiled-content',
    template: template,
    directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]
})
export default class ContentView {

    static parameters = [ [ Router ], [ ContentService ], [RouteParams], [DynamicComponentLoader] , [ElementRef]];

    router:Router;
    contentService:ContentService;
    routeParams: RouteParams;
    dynamicComponentLoader: DynamicComponentLoader;
    elementRef:ElementRef;

    constructor( router:Router, contentService:ContentService, routeParams:RouteParams ,  dynamicComponentLoader: DynamicComponentLoader, elementRef:ElementRef) {

        console.log(">> ContentView -> constructed");

        this.router = router;
        this.contentService = contentService;
        this.routeParams = routeParams;
        this.dynamicComponentLoader = dynamicComponentLoader;
        this.elementRef = elementRef;

        let id = this.routeParams.get('id');

        console.log("-- ContentView -> Got id: " + id);


        /*
         let document = this.contentService.getDocumentById(id).then(
         ( doc ) => {
         console.log("-- ContentView -> document: " + doc);
         }
         ).catch( console.error );
         */


        /*
        @Component({
            selector: 'compiled-component',
            //directives: [CodeMirrorComponent.Class]
        })
        @View({ templateUrl: 'http://127.0.0.1:8080/assets/documents/' + id + '.html'})
        class CompiledComponent {

            testProperty:string = "component in scope";

        };

        dynamicComponentLoader.loadIntoLocation(CompiledComponent, elementRef, 'container');
        */

        /*
        content:string = `hello mojo`;

        let contentComponent:() => {} = function () {
            //this.content = "Nothing; I'm just a dummy!";
        };
        Reflect.decorate( [ Component( {template: content, directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]} ) ], contentComponent );
        this.dynamicComponentLoader.loadIntoLocation( contentComponent, this.elementRef, "container" );
        */

        this.contentService.getDocumentById( id ).then(
            ( content )=> {
                //this.blogPost.content = content;
                let contentComponent:() => {} = function () {
                    //this.content = "Hello!";
                };
                Reflect.decorate( [ Component( {template: content, directives: [ CORE_DIRECTIVES, ROUTER_DIRECTIVES, CodeMirrorComponent.Class ]} ) ], contentComponent );
                this.dynamicComponentLoader.loadIntoLocation( contentComponent, this.elementRef, "container" );
            }
        );



    }

}