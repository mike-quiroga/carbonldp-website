import {Component, ElementRef } from 'angular2/core';
import { CORE_DIRECTIVES } from 'angular2/common';
import { Title } from 'angular2/platform/browser';


import * as CodeMirrorComponent from "app/components/code-mirror/CodeMirrorComponent";

import $ from 'jquery';
import 'semantic-ui/semantic';
import SideBarComponent from 'app/documents/Sidebar/SideBarComponent';

import template from './template.html!';
import "./style.css!";


@Component( {
	selector: 'getting-started-rest-api',
	template: template,
	directives: [ CORE_DIRECTIVES,SideBarComponent, CodeMirrorComponent.Class],
	providers: [ Title ]
} )
export default class GettingStartedWithTheRestApiView {
	static parameters = [ [ ElementRef ], [ Title ] ];

	element:ElementRef;
	$element:JQuery;
	title:Title;

	constructor( element:ElementRef, title:Title ) {
		this.element = element;
		this.title = title;
		this.title.setTitle( "Getting started - Rest API" );
	}


	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
		this.createAccordions();
	}

	createAccordions():void {
		this.$element.find( '.ui.accordion' ).accordion();
	}




}