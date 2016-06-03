import { Injectable, EventEmitter } from "@angular/core";

@Injectable()
export default class SidebarService {
	buildEmitter:EventEmitter<any> = new EventEmitter();

	constructor() { }

	build():void {
		this.buildEmitter.emit( null );
	}
}
