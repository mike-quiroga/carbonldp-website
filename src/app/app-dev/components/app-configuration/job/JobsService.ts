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

@Injectable()
export default class JobsService {

	carbon:Carbon;

	jobs:Map<string, PersistedDocument.Class>;

	constructor( carbon:Carbon ) {
		this.carbon = carbon;
		this.jobs = new Map<string, PersistedDocument.Class>();
	}

	getJobOfType( type:string, appContext:SDKContext.Class ):Promise<PersistedDocument.Class> {
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

	getAll( appContext:SDKContext.Class ):Promise<PersistedDocument.Class[]> {
		let uri:string = (<App.Context>appContext).app.id + "jobs/";
		return this.carbon.documents.getChildren( uri ).then( ( [jobs, response]:[PersistedDocument.Class[], HTTP.Response.Class] ) => {
			jobs.filter( ( job:PersistedDocument.Class ) => ! this.jobs.has( job.id ) )
				.forEach( ( job:PersistedDocument.Class ) => this.jobs.set( job.id, job ) );
			return Utils.A.from( this.jobs.values() );
		} );
	}

	createJob( type:string, appContext:SDKContext.Class ):Promise<Response.Class> {
		let uri:string = (<App.Context>appContext).app.id + "jobs/";
		let requestOptions:HTTP.Request.Options = { sendCredentialsOnCORS: true, };
		if ( appContext && appContext.auth.isAuthenticated() ) appContext.auth.addAuthentication( requestOptions );
		HTTP.Request.Util.setAcceptHeader( "application/ld+json", requestOptions );
		HTTP.Request.Util.setPreferredInteractionModel( NS.LDP.Class.Container, requestOptions );
		HTTP.Request.Util.setContentTypeHeader( "text/turtle", requestOptions );
		let body:string =
			`@prefix c:  <https://carbonldp.com/ns/v1/platform#>.
			<>
			a c:ImportBackupJob.`;

		return HTTP.Request.Service.post( uri, body, requestOptions ).then( ( response:Response.Class ) => {
			return response;
		} ).catch( ( error ) => {
			console.error( error );
			return Promise.reject( error );
		} );
	}
}
