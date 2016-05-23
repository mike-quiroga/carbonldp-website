import { Injectable, EventEmitter } from "@angular/core";

import { Message } from "./../ErrorsAreaComponent";

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
