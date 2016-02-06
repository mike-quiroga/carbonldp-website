import { Provider } from 'angular2/core';

import ContentService from 'app/content/ContentService';

export const CONTENT_PROVIDERS = [
    new Provider( ContentService, {
        useClass: ContentService,
        dependencies: ContentService.dependencies
    } )
];