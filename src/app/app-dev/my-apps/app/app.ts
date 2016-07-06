import * as App from "carbonldp/App";

export interface App {
	name:string;
	slug:string;
	created:Date;
	modified:Date;
	appContext:App.Context;
}

export default App;
