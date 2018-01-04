---
title: A great backend for your next Angular app
date: 2017-03-30 19:19:35
avatar: team/alvaro-contreras-avatar-blog.jpg
author: Alvaro Contreras
tags:
---



What makes Carbon LDP a good fit for Angular applications?

Let’s start with the initial pain you always face when building a new data-centric web app - *the plumbing*. You’ve got a lot of gritty work to do before you can have some real fun with the sexy front-end.
<!-- more -->
You need a database server and a database. You’ll want a decent schema design, which means you’ll have to think carefully about everything the app needs to do. Depending on what you might use on the server-side, your schema’s likely going to be tied to a corresponding model in compiled, server-side objects. In addition to the server-side code model, you might also end up with a lot of SQL queries that create a hard-coupling between your model and schema. Just setting all of this up is a pain, much less making changes down the road.

Carbon LDP gives you a server and database out-of-the-box. What’s more, it’s all neatly contained in a Docker container, which you can download and run with just a couple of commands. You can have a database ready and running in minutes rather than hours or days.

You won’t need to worry as much about schema design. Carbon LDP provides schema-less flexibility. You can add new objects and properties with no need for schema changes, code compilation, or package deployment. You’ll spend less time with up-front design and you’ll be able to evolve your application more easily as requirements change.

You can forget about the server-side object model, deployable code, microservices and what-not. With Carbon, you can deal with most of it directly in JavaScript, right alongside your Angular code. Using Carbon’s JavaScript SDK or REST API, you can persist any arbitrary JavaScript/JSON object to the database. You can then fetch, update and query across multiple objects. What’s more, each object that you persist becomes a RESTful end-point with its own automatic API, which makes your data easier to access.


In addition to the Platform, Carbon also provides a Workbench, a graphical user interface, that enables you to explore and work with data as you code. The Workbench itself is, in fact, a pure Angular application that runs on Carbon - a great example of what you can achieve when you put Carbon and Angular together. You can examine our Angular code for the Workbench and find useful boilerplates on our [GitHub page](https://github.com/CarbonLDP).

And what about security?

Typically, data-centric applications need to be secure. Carbon provides services for authentication and role-based access control. By using them, you can spend your precious time with Angular and leave the complexities of authentication and authorization to Carbon. If you want, you can also use the [angular-carbonldp](https://www.npmjs.com/package/angular-carbonldp) library to easily secure app routes to authenticated users.

Lastly, when consuming the data of your app, you must develop front-end services which, even with Angular, must be handmade - meaning that you must write each create, read, update or delete (CRUD) request for each object type in the database. That development time is reduced when using Carbon’s SDK. Instead of writing your own HTTP requests, you can simply call ready-made CRUD functions available for your custom objects by way of the SDK.

In short, Carbon LDP provides all the back-end services you need out-of-the-box, so that you can develop data-centric Angular web apps faster and easier. If that sounds good to you, why not [Get Started](https://carbonldp.com/get-started/) now?

## Carbon LDP at NG-Conf

This year, we’re taking Carbon LDP to [NG-Conf](https://www.ng-conf.org/), the World’s Original Angular Conference, in Salt Lake City, UT (April 5th-7th). If you’re planning to attend, we hope you’ll find us at stand #24, or you can get a glimpse of the action on the conference [Livestream](https://www.ng-conf.org/livestream/) or at one of the many [NG-Conf Extended](https://www.ng-conf.org/extended/) events taking part across the globe.

Join us and see how Carbon LDP could be the hottest new addition to your Angular application stack.
