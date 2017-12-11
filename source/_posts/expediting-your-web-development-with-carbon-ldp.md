---
title: Expediting your web development with Carbon LDP
date: 2017-12-05 2:44:39
avatar: alvaro-contreras-avatar-blog.jpg
author: Alvaro Contreras
tags:
---

With web development, developers face a lot of difficulties when they have to provide a full-stack solution to a client. Reducing these complexities should be a top line priority for everyone involved. Let's look at one way to simplify development.
<!-- more -->

When providing a full stack solution, developers must build a back-end layer own their own, thus requiring a lot of excess development time. Specifically, this means they have to follow tedious steps:

1. Design the database structure (schema)
2. Build the database from the schema
3. Build an API to manipulate the database
4. Build a front-end service layer to consume said API
5. Build the user interfaces using the service layer

Steps one through three make up the back-end work required of web developers during these projects. This means that only then – after they completed these steps - can they now start to code the front-end (customer facing) side of the solution.

But what happens when the time-consuming database schema requires a change? For example, a new requirement pops up during one of the client meetings (which is a common aspect of agile methodologies) or a problem in your initial design is found. These problems would require the developer to start at the beginning once again to modify the database structure, re-build the database, expose the changes through the API and code the front-end service to consume the API. And what happens when another change pops up? Rinse and repeat.

As you may have noticed, this consumes a lot of time and effort from both parties, the back-end and the front-end side of the web development area. This can be problematic if you don't have a lot of resources to dedicate to the back-end tasks.

Carbon LDP was designed to provide a schema-less database that also comes with a standardized RESTful API to tackle precisely these kinds of problems.

> *This means that whenever you require a database modification, you just add/remove the properties in the database, without having to go through steps one through three again.*
> *No more rinse and repeat!*

Another potential issue that Carbon LDP tackles are the risk of writing a non-compliant/standardized RESTful API by less experienced back-end developers. This is possible because Carbon strictly adheres to the W3C's HTTP Method Definitions guidelines, meaning that you will no longer face the risk of providing an API that may not be complying with a worldwide standard.

Using Carbon LDP means that you would only have to do the following when creating a Web app:

1. Build a front-end service layer to consume said API
2. Build the user interfaces using the service layer

![Comparison with-without Carbon LDP](/assets/images/With-and-without-Carbon.png)

In essence, Carbon LDP can save a dev team a considerable amount of time by reducing the amount of unnecessary and redundant work.

Additionally, Carbon LDP provides a JavaScript SDK that comes with an integrated service layer to consume the API. No more coding of client-side HTTP calls, leaving you with time to focus on coding the user interfaces!

Further, Carbon LDP is a semantic linked-data platform. Stored data can easily be shared across multiple computers, allowing machines to also comprehend what the linked data means without having to apply more logic to understand it.

Carbon LDP will save a lot of time and effort with cumbersome development tasks. Now there is more time for the developer to focus on providing a well-developed application that the client and end-user will love!

Want to learn more about Carbon? [Contact Base22 here](https://base22.com/wps/portal/base22/contact/contact), or just **[get started](https://carbonldp.com/documentation/v1.0.x/quick-start-guide/)** with Carbon right now!

