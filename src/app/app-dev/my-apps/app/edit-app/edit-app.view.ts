import { Component, ElementRef, Host, Inject, forwardRef } from "@angular/core";
import { CORE_DIRECTIVES } from "@angular/common";
import { Router } from "@angular/router-deprecated";

import $ from "jquery";
import "semantic-ui/semantic";

import * as App from "./../app";

import { AppDetailView } from "./../app-detail.view";
import EditAppComponent from "./edit-app-component/EditAppComponent";
import ErrorsAreaService from "app/app-dev/components/errors-area/service/ErrorsAreaService";

import template from "./edit-app.view.html!";

@Component( {
	selector: "edit-app-view",
	template: template,
	directives: [ CORE_DIRECTIVES, EditAppComponent, ],
} )

export class EditAppView {
	router:Router;
	element:ElementRef;
	$element:JQuery;
	app:App.Class;
	private errorsAreaService:ErrorsAreaService;

	constructor( router:Router, element:ElementRef, errorsAreaService:ErrorsAreaService, @Host() @Inject( forwardRef( () => AppDetailView ) ) appDetail:AppDetailView ) {
		this.router = router;
		this.element = element;
		this.app = appDetail.app;
		this.errorsAreaService = errorsAreaService;
	}

	ngAfterViewInit():void {
		this.$element = $( this.element.nativeElement );
	}

	notifyErrorAreaService( error:any ):void {
		this.errorsAreaService.addError(
			error.title,
			error.content,
			error.statusCode,
			error.statusMessage,
			error.endpoint
		);
	}
}

export default EditAppView;
