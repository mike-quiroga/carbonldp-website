import { Provider } from 'angular2/angular2';

import AppDevComponent from 'app/app-dev/AppDevComponent';

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevComponent, {
		useClass: AppDevComponent,
		dependencies: AppDevComponent.dependencies
	} )
];