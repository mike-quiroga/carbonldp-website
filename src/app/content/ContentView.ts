import { Component, CORE_DIRECTIVES, DynamicComponentLoader, ElementRef, View} from 'angular2/angular2';
import { ROUTER_DIRECTIVES, ROUTER_PROVIDERS, Router, Instruction, RouteParams } from 'angular2/router';
import ContentService from 'app/content/ContentService';

import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from 'jquery';
import 'semantic-ui/semantic';

import template from './template.html!';
import ComponentRef = ng.ComponentRef;

import "./style.css!";

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
    $element;
    compiledComponent;

    constructor( router:Router, contentService:ContentService, routeParams:RouteParams ,  dynamicComponentLoader: DynamicComponentLoader, elementRef:ElementRef) {

        console.log(">> ContentView -> constructed");

        this.router = router;
        this.contentService = contentService;
        this.routeParams = routeParams;
        this.dynamicComponentLoader = dynamicComponentLoader;
        this.elementRef = elementRef;
        this.compiledComponent = CompiledComponent;

        let id = this.routeParams.get('id');

        // console.log("-- ContentView -> Got id: " + id);


        // START: OPTION A -------------------------------------------------------------------------
        // USE THIS TO LOAD A TEMPLATE DIRECTLY FROM TEMPLATE URL
        @Component({
            selector: 'compiled-component',
            directives: [CodeMirrorComponent.Class],
            //templateUrl: 'http://127.0.0.1:8080/assets/documents/' + id + '.html'
            templateUrl: '/assets/documents/' + id
        })
        class CompiledComponent {

            static parameters = [ [ElementRef]];

            elementRef:ElementRef;
            $element;

            $container;
            $sectionHeaders;
            $followMenu;

            host:string = "dev.carbonldp.com";

            constructor(elementRef:ElementRef) {
                this.elementRef = elementRef;
                this.$element = $( this.elementRef.nativeElement );


            }

            afterViewInit():void {
                this.createAccordions();
                this.evalJavascript();

                this.$container = $('article');
                this.$sectionHeaders = this.$container.children('h2');
                this.$followMenu = this.$element.find('.following.menu');

                let _self = this;

                this.$sectionHeaders.visibility({
                    observeChanges: false,
                    once: false,
                    offset: 50,
                    onTopPassed: function() {
                        _self.activateSection(this);
                    },
                    onTopPassedReverse: function() {
                        _self.activatePrevious();
                    }
                });
            }

            createAccordions():void {
                this.$element.find( '.ui.accordion' ).accordion();
            }

            // Enables the use of inline JavaScript by placing script in hidden DIV elements with class
            // "script".
            // <div class="script">...</div>
            // Yes, we know that Angular frowns upon this. It shouldn't be used for the wrong things.
            // But there ARE rare cases where this is handy.
            evalJavascript():void {
                let scripts:any[] = this.elementRef.nativeElement.querySelectorAll( ".script" );
                let i:number = 0, scriptLength = scripts.length;
                for ( i; i < scriptLength; i ++ ) {
                    eval( scripts[ i ].textContent );
                }
            }

            // Expands accordion sections as you scroll through the H2 elements
            // on the page.
            activateSection(elm):void {

                console.log(">> activateSection()");

                var
                    $section       = $(elm),
                    index          = this.$sectionHeaders.index($section),
                    $followSection = this.$followMenu.children('.item'),
                    $activeSection = $followSection.eq(index),
                    isActive       = $activeSection.hasClass('active')
                    ;
                if(!isActive) {
                    $followSection.filter('.active')
                        .removeClass('active')
                    ;
                    $activeSection
                        .addClass('active')
                    ;
                    this.$followMenu
                        .accordion('open', index)
                    ;
                }

            }

            // Contracts accordion sections as you scroll through the H2 elements
            // on the page.
            activatePrevious():void {

                console.log(">> activatePrevious()");

                var
                    $menuItems  = this.$followMenu.children('.item'),
                    $section    = $menuItems.filter('.active'),
                    index       = $menuItems.index($section)
                    ;
                if($section.prev().size() > 0) {
                    $section
                        .removeClass('active')
                        .prev('.item')
                        .addClass('active')
                    ;
                    this.$followMenu.accordion('open', index - 1);
                }

            }


        };

        dynamicComponentLoader.loadIntoLocation(CompiledComponent, elementRef, 'container');

        // END: OPTION A ---------------------------------------------------------------------------


        // START: OPTION B: ------------------------------------------------------------------------
        // USE THIS TO LOAD A TEMPLATE DYNAMICALLY FROM HTTP USING A SERVICE

        /*
        this.contentService.getDocumentById( id ).then(
            ( content )=> {
                @Component({
                    selector: 'compiled-component',
                    directives: [CodeMirrorComponent.Class],
                    template: content
                })
                class CompiledComponent {

                    //testProperty:string = "component in scope";

                };

                dynamicComponentLoader.loadIntoLocation(CompiledComponent, elementRef, 'container');
            }
        );
        */

        // END: OPTION B --------------------------------------------------------------------------

    }

}

