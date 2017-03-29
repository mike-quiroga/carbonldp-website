---
title: A Great Backend for Your Next Angular App
date: 2017-03-20 11:53:06
author: Cody Burleson
tags:
---


This year, we’re taking Carbon LDP to NG-Conf, the World’s Original Angular Conference, in Salt Lake City, UT (April 5th-7th). If you’re planning to attend, we hope you’ll find us at stand #24 and see how Carbon LDP could be the hottest new addition to your Angular application stack.

NG-Conf was the first ever Angular framework conference. Last year (its third year), brought more than 1500 developers from across the industry to participate in training sessions presented by the entire Google Angular team and other Angular experts. This year, attendance is expected to reach 1600. The conference has had overwhelming popularity and a fast ticket sellout, but if you’ve missed your chance, you might still get a glimpse of action on the conference [Livestream page](https://www.ng-conf.org/livestream/) or at one of the many [NG-Conf Extended](https://www.ng-conf.org/extended/) events taking part across the globe.

## Why Carbon LDP + Angular?

So, what makes Carbon LDP a good fit for Angular applications? Let’s start with the first pain-points you always face when building a new data-centric web app - the plumbing. You’ve got a lot of gritty work to do before you can have some real fun with the sexy front-end.

You need a database server and a database. You’ll want a decent schema design, which means you’ll have to think carefully about everything the app needs to do. Depending on what you might use on the server-side, your schema’s likely going to be tied to a corresponding model in compiled, server-side objects. In addition to the server-side code model, you might also end up with a lot of SQL queries that create a hard-coupling between your model and schema. Just setting all of this up is a pain, much less making changes down the road.

Carbon LDP gives you a server and database out-of-the-box. What’s more, it’s all neatly contained in a Docker container, which you can download and run with just a couple of commands. You can have a database ready and running in minutes rather than hours or days.

You won’t need to worry as much about schema design. Carbon LDP provides schema-less flexibility. You can add new objects and properties with no need for schema changes, code compilation, or package deployment. You’ll spend less time with up-front design and you’ll be able to evolve your application more easily as requirements change.

You can forget about the server-side object model, deployable code, microservices and what-not. With Carbon, you can deal with most of it directly in JavaScript, right alongside your Angular code. Using Carbon’s JavaScript SDK or REST API, you can persist any arbitrary JavaScript/JSON object to the database. You can then fetch, update and query across multiple documents. What’s more, each document that you persist becomes a RESTful end-point with its own automatic API.

In addition to the Platform, Carbon also provides a Workbench ( also easy to download and run as a Docker container). The Workbench provides a graphical user interface that enables you to explore and work with data as you code. The Workbench itself is, in fact, a pure Angular application that runs on Carbon. It’s a great example of what you can achieve when you put Carbon and Angular together. You can examine our Angular code for the Workbench and find useful boilerplates on our [GitHub page](https://github.com/CarbonLDP).

In short, Carbon LDP provides all the back-end you need out-of-the-box, so that you can develop data-centric Angular web apps faster and easier. If that sounds good to you, why not [Get Started](https://carbonldp.com/get-started/) now?



