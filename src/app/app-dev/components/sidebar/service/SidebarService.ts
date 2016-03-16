import { Injectable, EventEmitter } from "angular2/core";

import App from "app/app-dev/my-apps/app/App"
import SidebarItem from "./../SidebarItem"

@Injectable()
export default class SidebarService {
	data:string;
	$element:JQuery;

	addAppEmitter:EventEmitter<any> = new EventEmitter();
	toggleEmitter:EventEmitter<any> = new EventEmitter();
	toggleMenuButtonEmitter:EventEmitter<any> = new EventEmitter();

	constructor() { }

	addItem( name:string, url?:string, icon?:string ):void {
		let item = new SidebarItem();
		item.name = name;
		item.url = url ? url : null;
		item.icon = icon ? icon : null;
	}

	addApp( app:App ) {
		this.addAppEmitter.emit( app );
	}

	toggle():void {
		this.toggleEmitter.emit( null );
	}

}
