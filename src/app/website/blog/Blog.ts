import { Provider } from "@angular/core";

import BlogService from "./service/blog.service";

export const BLOG_PROVIDERS = [
	new Provider( BlogService, {
		useClass: BlogService
	} )
];