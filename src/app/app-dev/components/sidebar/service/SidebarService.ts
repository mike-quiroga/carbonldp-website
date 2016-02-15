import { Injectable, EventEmitter } from 'angular2/core';

import CarbonApp from 'app/app-dev/my-apps/carbon-app/CarbonApp'
import SidebarItem from './../SidebarItem'

@Injectable()
export default class SidebarService {

	static parameters = [];
	static dependencies = SidebarService.parameters;

	data:string;
	$element:JQuery;

	addCarbonAppEmitter:EventEmitter = new EventEmitter();
	toggleEmitter:EventEmitter = new EventEmitter();

	constructor() {

	}

	addItem( name:string, url?:string, icon?:string ):void {
		let item = new SidebarItem();
		item.name = name;
		item.url = url ? url : null;
		item.icon = icon ? icon : null;
		//this.addItemEmitter.next( item );
	}

	addCarbonApp( app:CarbonApp ) {
		this.addCarbonAppEmitter.next( app );
	}

	remove():void {

	}

	toggle():void {
		this.toggleEmitter.next( null );
	}

}
