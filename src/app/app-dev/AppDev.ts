import { Provider } from 'angular2/core';

import AppDevComponent from 'app/app-dev/AppDevComponent';

export const APP_DEV_PROVIDERS = [
	new Provider( AppDevComponent, {
		useClass: AppDevComponent,
		dependencies: AppDevComponent.dependencies
	} )
];