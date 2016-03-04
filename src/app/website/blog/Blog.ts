/// <reference path="./../../../../typings/typings.d.ts" />
import { Provider } from "angular2/core";

import BlogService from "./service/BlogService";

export const BLOG_PROVIDERS = [
	new Provider( BlogService, {
		useClass: BlogService
	} )
];