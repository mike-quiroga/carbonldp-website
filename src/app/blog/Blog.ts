import { Provider } from 'angular2/angular2';

import BlogPostService from 'app/blog/posts/BlogPostService';

export const BLOG_PROVIDERS = [
	new Provider( BlogPostService, {
		useClass: BlogPostService,
		dependencies: BlogPostService.dependencies
	} )
];