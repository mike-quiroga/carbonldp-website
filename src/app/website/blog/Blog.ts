import { Provider } from "@angular/core";

import BlogService from "./service/BlogService";

export const BLOG_PROVIDERS = [
	new Provider( BlogService, {
		useClass: BlogService
	} )
];