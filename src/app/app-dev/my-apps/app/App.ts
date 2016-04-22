import * as CarbonApp from "carbonldp/App";

export interface App extends CarbonApp {
	name:string;
	created:string;
	modified:string;
	slug:string;
	description:string;
	app:CarbonApp.Context;
}

export default App;
