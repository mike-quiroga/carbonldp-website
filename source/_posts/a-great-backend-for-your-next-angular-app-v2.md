---
title: A Great Backend for Your Next Angular App v2
date: 2017-03-29 19:19:35
avatar:
author: Cody Burleson & Alvaro Contreras
tags:
---



What makes Carbon LDP a good fit for Angular applications? Let’s start with the first pain-points you always face when building a new data-centric web app - the plumbing. You’ve got a lot of gritty work to do before you can have some real fun with the sexy front-end.
<!-- more -->
You need a database server and a database. You’ll want a decent schema design, which means you’ll have to think carefully about everything the app needs to do. Depending on what you might use on the server-side, your schema’s likely going to be tied to a corresponding model in compiled, server-side objects. In addition to the server-side code model, you might also end up with a lot of SQL queries that create a hard-coupling between your model and schema. Just setting all of this up is a pain, much less making changes down the road.

Carbon LDP gives you a server and database out-of-the-box. What’s more, it’s all neatly contained in a Docker container, which you can download and run with just a couple of commands. You can have a database ready and running in minutes rather than hours or days.

You won’t need to worry as much about schema design. Carbon LDP provides schema-less flexibility. You can add new objects and properties with no need for schema changes, code compilation, or package deployment. You’ll spend less time with up-front design and you’ll be able to evolve your application more easily as requirements change.

You can forget about the server-side object model, deployable code, microservices and what-not. With Carbon, you can deal with most of it directly in JavaScript, right alongside your Angular code. Using Carbon’s JavaScript SDK or REST API, you can persist any arbitrary JavaScript/JSON object to the database. You can then fetch, update and query across multiple objects. What’s more, each object that you persist becomes a RESTful end-point with its own automatic API, which makes your database easier to access thanks to the out-of-the-box REST API.


In addition to the Platform, Carbon also provides a Workbench, a graphical user interface, that enables you to explore and work with data as you code. All of this by downloading and running a simple Docker container). The Workbench itself is, in fact, a pure Angular application that runs on Carbon which makes a great example of what you can achieve when you put Carbon and Angular together. You can examine our Angular code for the Workbench and find useful boilerplates on our [GitHub page](https://github.com/CarbonLDP).

## And what about security?
Typically, you must dedicate in your database schema design a way to handle authentication. And again, once you’ve finished your database schema and your SQL queries, you proceed to create your models in compiled server side objects, so they can then be exposed through a custom-made API, which can later be consumed by your Angular app. All of that represents a lot of work that can be avoided if you use the out-of-the-box features (database, security, RESTAPI) that Carbon provides. By using them, you can simply concentrate in developing your Angular app by leaving the authentication part to Carbon and, if you want, you can also use the angular-carbonldp library to easily secure your app routes to your authenticated users.

Lastly, when consuming the data of your app, you must develop your front-end services, which even with Angular must be handmade, meaning that you must code each create, read, update or delete (CRUD) request for each object living in the database. That consumes a lot of time and resources that can be reduced by using Carbon’s SDK, instead of writing your own HTTP requests, you can CRUD an object by simply calling those instructions with the SDK, saving you time and resources that can be better used to develop your front-end.


In short, Carbon LDP provides all the back-end you need out-of-the-box, so that you can develop data-centric Angular web apps faster and easier. If that sounds good to you, why not [Get Started](https://carbonldp.com/get-started/) now?

## Carbon LDP at NG-conf

This year, we’re taking Carbon LDP to [NG-Conf](https://www.ng-conf.org/), the World’s Original Angular Conference, in Salt Lake City, UT (April 5th-7th). If you’re planning to attend, we hope you’ll find us at stand #24, or you can get a glimpse of the action on the conference [Livestream](https://www.ng-conf.org/livestream/) or at one of the many [NG-Conf Extended](https://www.ng-conf.org/extended/) events taking part across the globe.

Join us and see how Carbon LDP could be the hottest new addition to your Angular application stack.








