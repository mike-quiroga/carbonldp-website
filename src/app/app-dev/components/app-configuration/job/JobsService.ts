import { Injectable } from "angular2/core";

import Carbon from "carbonldp/Carbon";
import * as App from "carbonldp/App";
import * as HTTP from "carbonldp/HTTP";
import * as Response from "carbonldp/HTTP/Response";
import * as NS from "carbonldp/NS";
import * as SDKContext from "carbonldp/SDKContext";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as URI from "carbonldp/RDF/URI";
import * as Utils from "carbonldp/Utils";
import * as Pointer from "carbonldp/Pointer";

import * as Job from "./Job"

@Injectable()
export default class Class {

	carbon:Carbon;

	jobs:Map<string, PersistedDocument.Class>;

	constructor( carbon:Carbon ) {
		this.carbon = carbon;
		this.jobs = new Map<string, PersistedDocument.Class>();
	}

	getJobOfType( type:string, appContext:App.Context ):Promise<PersistedDocument.Class> {
		if ( ! type ) return <any> Promise.reject( new Error( "Provide a job type." ) );
		if ( ! appContext ) return <any> Promise.reject( new Error( "Provide an appContext." ) );
		let jobsArray:PersistedDocument.Class[] = Utils.A.from( this.jobs.values() );
		let job:PersistedDocument.Class = jobsArray.find( ( job:PersistedDocument.Class ) => job.types.indexOf( type ) !== - 1 );
		if ( ! ! job ) return Promise.resolve( job );

		return this.getAll( appContext ).then(
			( jobs:PersistedDocument.Class[] )=> {
				let jobsArray:PersistedDocument.Class[] = Utils.A.from( this.jobs.values() );
				return jobsArray.find( ( job:PersistedDocument.Class ) => job.types.indexOf( type ) !== - 1 );
			}
		);
	}

	getAll( appContext:App.Context ):Promise<PersistedDocument.Class[]> {
		let uri:string = appContext.app.id + "jobs/";
		return this.carbon.documents.getChildren( uri ).then( ( [jobs, response]:[PersistedDocument.Class[], HTTP.Response.Class] ) => {
			jobs.filter( ( job:PersistedDocument.Class ) => ! this.jobs.has( job.id ) )
				.forEach( ( job:PersistedDocument.Class ) => this.jobs.set( job.id, job ) );
			return Utils.A.from( this.jobs.values() );
		} );
	}

	createExportBackup( appContext:App.Context ):Promise<HTTP.Response.Class> {
		let uri:string = appContext.app.id + "jobs/";
		let requestOptions:HTTP.Request.Options = { sendCredentialsOnCORS: true, };
		if ( appContext && appContext.auth.isAuthenticated() ) appContext.auth.addAuthentication( requestOptions );
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.Container, requestOptions );
		HTTP.Request.Util.setContentTypeHeader( "text/turtle", requestOptions );
		let body:string = `<> a <${Job.Type.EXPORT_BACKUP}>.`;
		return HTTP.Request.Service.post( uri, body, requestOptions ).then( ( response:Response.Class ) => {
			return response;
		} ).catch( ( error ) => {
			console.error( error );
			return Promise.reject( error );
		} );
	}

	createImportBackup( backupURI:string, appContext:App.Context ):Promise<PersistedDocument.Class> {
		return new Promise<PersistedDocument.Class>(
			( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
				let uri:string = appContext.app.id + "jobs/";
				let tempJob:any = {};
				tempJob[ "types" ] = [ Job.Type.IMPORT_BACKUP ];
				tempJob[ Job.namespace + "backup" ] = appContext.documents.getPointer( backupURI );
				appContext.documents.createChild( uri, tempJob ).then(
					( [pointer, response]:[Pointer.Class, Response.Class] )=> {
						pointer.resolve().then( ( [importJob, response]:[PersistedDocument.Class, HTTP.Response.Class] )=> resolve( importJob ) );
					} ).catch( ( error )=> reject( error ) );
			}
		);
	}

	runJob( job:PersistedDocument.Class ):Promise<PersistedDocument.Class> {
		return new Promise<PersistedDocument.Class>(
			( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
				let tempJob:any = {};
				tempJob[ "types" ] = [ Job.namespace + "Execution" ];
				this.carbon.documents.createChild( job.id, tempJob ).then(
					( [pointer, response]:[Pointer.Class, Response.Class] )=> {
						pointer.resolve().then(
							( [importJob, response]:[PersistedDocument.Class, HTTP.Response.Class] )=> {
								console.log( importJob );
								resolve( importJob );
							}
						)
					} ).catch( ( error )=> reject( error ) );
			}
		);
	}

	checkJobExecution( jobExecution:PersistedDocument.Class ):Promise<PersistedDocument.Class> {
		return new Promise<PersistedDocument.Class>(
			( resolve:( result:any ) => void, reject:( error:Error ) => void ) => {
				this.carbon.documents.get( jobExecution[ Job.namespace + "status" ].id ).then(
					( [resolvedJobExecution, response]:[PersistedDocument.Class, HTTP.Response.Class] )=> {
						resolve( resolvedJobExecution );
					} ).catch( ( error )=> reject( error ) );
			}
		);
	}
}
