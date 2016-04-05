import { Injectable, EventEmitter } from "angular2/core";

import App from "app/app-dev/my-apps/app/App";
import {Message} from "./../ErrorsAreaComponent";

@Injectable()
export default class ErrorsAreaService {
	data:string;
	$element:JQuery;

	addErrorEmitter:EventEmitter<any> = new EventEmitter();

	constructor() { }


	addError( title:string, content:string, statusCode:string, statusMessage:string, endpoint:string ):void {
		let message:Message = <Message>{
			title: title,
			content: content,
			statusCode: statusCode,
			statusMessage: statusMessage,
			endpoint: endpoint,
		};
		this.addErrorEmitter.emit( message );
	}
}
