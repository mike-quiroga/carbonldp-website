import { Component, AfterViewInit } from "@angular/core";

import Carbon from "carbonldp/Carbon";
import * as Token from "carbonldp/Auth/Token";
import * as App from "carbonldp/App";
import * as PersistedAccessPoint from "carbonldp/PersistedAccessPoint";
import * as PersistedDocument from "carbonldp/PersistedDocument";
import * as Response from "carbonldp/HTTP/Response";

class Task {
	name:string;
	dueDate:Date;
}

class Person {
	firstName:string;
	lastName:string;
	age:number;

	project?:Project;
}

class Project {
	name:string;
	members?:Person[] | Person;
	tasks?:Task[];
}

@Component( {
	selector: "my-component",
	template: `
		<div class="my-component__label">
			<ng-content></ng-content>
		</div>
	`,
	styles: [ `
		.my-component__label {
			border-width: 1px !important;
			border-style: solid !important;
			border-color: #333;
			padding: 15px !important;
			
			background-color: #555;
			
			color: #fff;
			
			font-size: 1em !important;
		}
	` ]
} )
export class MyComponent {

}

@Component( {
	selector: "test",
	template: `
		<my-component>
			Hello
		</my-component>
	`,
	styles: [ `
		:host {
			display: block;
			padding: 200px;
		}
		/deep/ .my-component__label {
			border-width: 2px;
			border-color: #000;
			padding: 5px;
			
			background-color: #333;
			
			color: #fff;
			
			font-size: 2em;
		}
	` ],
	directives: [ MyComponent ],
	providers: []
} )
export class TestView implements AfterViewInit {
	ngAfterViewInit():void {
		let carbon:Carbon = new Carbon();
		carbon.setSetting( "domain", "local.carbonldp.com" );

		let appContext:App.Context;
		let testObject:any;

		carbon.auth.authenticate( "example@example.com", "example" ).then( ( token:Token.Class ) => {
			console.log( "Authentication Token: %o", token );
			console.log( "%o", carbon.auth.authenticatedAgent );

			return carbon.apps.getContext( "test-app/" );
		} ).then( ( _appContext:App.Context ) => {
			appContext = _appContext;
			console.log( "App Context: %o", appContext );

			testObject = {
				types: [ "Example" ]
			};

			return appContext.documents.createChildAndRetrieve<any>( "/", testObject );
		} ).then( ( [ persistedObject, response ]:[ any & PersistedDocument.Class, Response.Class ] ) => {
			console.log( "%o", persistedObject );

			console.log( persistedObject.hasType( "Example" ) );

			persistedObject.types.push( "Example2" );

			return persistedObject.save();
		} ).then( ( response:Response.Class ) => {
			console.log( "Refreshing the project..." );
			return testObject.refresh();
		} ).then( ( [ persistedObject, response ]:[ PersistedDocument.Class, Response.Class ] ) => {
			console.log( persistedObject.hasType( "Example" ) );
			console.log( persistedObject.hasType( "Example2" ) );
			console.log( "Property1: %o", persistedObject.types );
		} ).catch( console.error );
	}

	ngAfterViewInits():void {
		let project:Project & { hasMemberRelation:string, isMemberOfRelation:string } = {
			name: "Project X",

			hasMemberRelation: "tasks",
			isMemberOfRelation: "project",
		};
		let task1:Task = {
			name: "Task 1",
			dueDate: new Date( "2016-04-03" )
		};
		let task2:Task = {
			name: "Task 2",
			dueDate: new Date( "2016-04-06" )
		};
		let peopleAP:{ hasMemberRelation:string, isMemberOfRelation:string } = {
			hasMemberRelation: "members",
			isMemberOfRelation: "project",
		};
		let person1:Person = {
			firstName: "John",
			lastName: "Cena",
			age: 35
		};
		let person2:Person = {
			firstName: "Michael",
			lastName: "Cera",
			age: 40
		};

		let carbon:Carbon = new Carbon();
		carbon.setSetting( "domain", "local.carbonldp.com" );

		let appContext:App.Context;
		let projects:PersistedDocument.Class;
		let persistedProject:Project & PersistedDocument.Class;
		let persistedPeopleAP:PersistedAccessPoint.Class;

		console.log( "Authenticating agent..." );
		carbon.auth.authenticate( "example@example.com", "example" ).then( ( token:Token.Class ) => {
			console.log( "Authentication Token: %o", token );
			console.log( "%o", carbon.auth.authenticatedAgent );

			console.log( "Retrieving app context..." );
			return carbon.apps.getContext( "test-app/" );
		} ).then( ( _appContext:App.Context ) => {
			appContext = _appContext;
			console.log( "App Context: %o", appContext );

			console.log( "Creating projects container..." );
			return appContext.documents.createChild( "/", {} );
		} ).then( ( [ _projects, response ]:[ PersistedDocument.Class, Response.Class ] ) => {
			projects = _projects;

			console.log( "Projects container: %o", projects );

			console.log( "Creating project..." );
			return projects.createChild<Project>( project, "My suP4 Projecto" );
		} ).then( ( [ _persistedProject, response ]:[ Project & PersistedDocument.Class, Response.Class ] ) => {
			persistedProject = _persistedProject;

			console.log( "PersistedProject: %o", persistedProject );
			console.log( "PersistedProject is equal to Project: %s", <any>project === persistedProject ); // true
			console.log( persistedProject.id ); // document's URI

			console.log( "Creating task1 and task2..." );
			return Promise.all( [ persistedProject.createChild( task1 ), persistedProject.createChild( task2 ) ] );
		} ).then( ( result:[ PersistedDocument.Class, Response.Class ][] ) => {
			console.log( "Task 1: %o", result[ 0 ][ 0 ] );
			console.log( "Task 1: %o", result[ 1 ][ 0 ] );

			console.log( "Refreshing Project..." );
			return persistedProject.refresh();
		} ).then( ( [ _persistedProject, response ]:[ Project & PersistedDocument.Class, Response.Class ] ) => {

			console.log( "RefreshedProject is equal to PersistedProject: %s", <any>persistedProject === _persistedProject ); // true

			console.log( "PersistedProject has task1 and task2: %s", persistedProject.tasks.indexOf( task1 ) !== - 1 && persistedProject.tasks.indexOf( task2 ) !== - 1 );

			console.log( "Creating people access point..." );
			return persistedProject.createAccessPoint( peopleAP, "people" );
		} ).then( ( [ _persistedPeopleAP, response ]:[ PersistedAccessPoint.Class, Response.Class ] ) => {
			persistedPeopleAP = _persistedPeopleAP;

			console.log( "PeopleAP is equal to PersistedPeopleAP: %s", <any>peopleAP === persistedPeopleAP ); // true

			console.log( "Creating person1 and person2..." );
			return Promise.all( [ appContext.documents.createChild( "/", person1 ), appContext.documents.createChild( "/", person2 ) ] );
		} ).then( ( result:[ PersistedDocument.Class, Response.Class ][] ) => {

			console.log( "Adding person1 and person2 as members of the access point..." );
			return persistedPeopleAP.addMembers( [ result[ 0 ][ 0 ], result[ 1 ][ 0 ] ] );
		} ).then( ( response:Response.Class ) => {
			console.log( "Refreshing the project..." );
			return persistedProject.refresh();
		} ).then( ( [ _persistedProject, response ]:[ Project & PersistedDocument.Class, Response.Class ] ) => {

			console.log( "RefreshedProject is equal to PersistedProject: %s", <any>persistedProject === _persistedProject ); // true

			console.log( "PersistedProject has person1 and person2: %s", persistedProject.members.indexOf( person1 ) !== - 1 && persistedProject.members.indexOf( person2 ) !== - 1 );

			console.log( "Removing person1 from the member list..." );
			return persistedPeopleAP.removeMember( <any> person1 );
		} ).then( ( response:Response.Class ) => {
			console.log( "Refreshing the project..." );
			return persistedProject.refresh();
		} ).then( ( [ _persistedProject, response ]:[ Project & PersistedDocument.Class, Response.Class ] ) => {

			console.log( "RefreshedProject is equal to PersistedProject: %s", <any>persistedProject === _persistedProject ); // true

			console.log( "PersistedProject doesn't have person1 but has person2: %s", persistedProject.members === person2 );

			console.log( "Refreshing person1 and person2..." );
			return Promise.all( [ (<any>person1).refresh(), (<any>person2).refresh() ] );
		} ).then( ( result:[ PersistedDocument.Class, Response.Class ][] ) => {

			console.log( "Person1 doesn't have project: %s", ! ( "project" in person1 ) );
			console.log( "Person2 has project: %s", person2.project === project );

		} ).catch( console.error );
	}
}

export default TestView;
