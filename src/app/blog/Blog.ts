import { Provider } from 'angular2/angular2';

import BlogService from 'app/blog/service/BlogService';

export const BLOG_PROVIDERS = [
	new Provider( BlogService, {
		useClass: BlogService,
		dependencies: BlogService.dependencies
	} )
];