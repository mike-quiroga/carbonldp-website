/// <reference path="./../../../../../../typings/typings.d.ts" />
import { Injectable, EventEmitter } from "angular2/core";

@Injectable()
export default class SidebarService {

	buildEmitter:EventEmitter<any> = new EventEmitter();

	constructor() { }

	build():void {
		this.buildEmitter.emit( null );
	}

}