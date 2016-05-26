import { Injectable } from "@angular/core";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";
import * as SDKContext from "carbonldp/SDKContext";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as Utils from "carbonldp/Utils";
import * as Pointer from "carbonldp/Pointer";

@Injectable()
export default class BackupsService {

	carbon:Carbon;

	backups:Map<string, PersistedDocument.Class>;


	constructor( carbon:Carbon ) {
		this.carbon = carbon;
		this.backups = new Map<string, PersistedDocument.Class>();
	}

	upload( file:Blob, appContext:SDKContext.Class ):Promise<[ Pointer.Class, HTTP.Response.Class ]> {
		let uri:string = (<App.Context>appContext).app.id + "backups/";
		return this.carbon.documents.upload( uri, file );
	}

	getAll( appContext:SDKContext.Class ):Promise<[PersistedDocument.Class[], HTTP.Response.Class]> {
		let uri:string = (<App.Context>appContext).app.id + "backups/";
		return this.carbon.documents.getChildren( uri ).then( ( [backups, response]:[PersistedDocument.Class[], HTTP.Response.Class] ) => {
			backups.filter( ( backup:PersistedDocument.Class ) => ! this.backups.has( backup.id ) )
				.forEach( ( backup:PersistedDocument.Class ) => this.backups.set( backup.id, backup ) );
			return [ Utils.A.from( this.backups.values() ), response ];
		} );
	}
}
