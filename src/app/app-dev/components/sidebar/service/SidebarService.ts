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
	//rxAddItemEmitter:any;
	toggleEmitter:EventEmitter = new EventEmitter();
	//rxtoggleEmitter:any;

	constructor() {
		//this.rxAddItemEmitter = this.addItemEmitter.toRx();
		//this.rxtoggleEmitter = this.toggleEmitter.toRx();
	}

	addItem( name:string, url?:string, icon?:string ):void {
		this.counter ++;
		let item = new SidebarItem();
		item.name = name;
		item.url = url ? url : null;
		item.icon = icon ? icon : null;
		//let app:CarbonApp = new CarbonApp();
		//app.creationDate = "21/Dec/2015";
		//app.name = "My App " + this.counter;

		this.addItemEmitter.next( item );
	}

	remove():void {
		this.counter --;
	}

	toggle():void {
		this.toggleEmitter.next( null );
	}

}
