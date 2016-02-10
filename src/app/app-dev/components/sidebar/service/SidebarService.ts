import { Injectable, EventEmitter } from 'angular2/core';

import CarbonApp from 'app/app-dev/my-apps/carbon-app/CarbonApp'
import SidebarItem from './../SidebarItem'

@Injectable()
export default class SidebarService {

	static parameters = [];
	static dependencies = SidebarService.parameters;

	data:string;
	$element:JQuery;

	counter:number = 0;
	addItemEmitter:EventEmitter = new EventEmitter();
	toggleEmitter:EventEmitter = new EventEmitter();

	constructor() {

	}

	addItem( name:string, url?:string, icon?:string ):void {
		this.counter ++;
		let item = new SidebarItem();
		item.name = name;
		item.url = url ? url : null;
		item.icon = icon ? icon : null;
		this.addItemEmitter.next( item );
	}

	remove():void {
		this.counter --;
	}

	toggle():void {
		this.toggleEmitter.next( null );
	}

}
