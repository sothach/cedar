
# User story
"As a busy developer, I need an easy way to create a to-do list to record stuff I've got to 
get done, in order that I do not forget anything important"

## UACs
- User can sign in using unique login and password. This can be hardcoded to a default user list  
user management functionality is not required. Please create at least one account for user 
test with password abc123. 
- User can view his/her current task list 
- User can check/uncheck any task on their list 
- User can add/remove task 
- All changes should be persistent  when a user signs into the system next time they will see 
changes made during previous session.
- Each task contains a text description and date when the entry was created. Both fields should 
be displayed in the UI.
- Since application can be used to by many users, the application should consider performance

## Definition of Done
* UACs satisfied
* Unit test coverage: 95%+
* i18n ready
* Appropriate error handling
* Operations logging
* Performance (jmeter'd)
* Documentation (design / release notes)

## Constraints
### Choice of framework 
App should be deployable as a war, in standard containers, such as Tomcat
Also assuming Java is the required language: this rules out Play! (requires Servlet 3.1 container,
and I've never built it to a war) and Lift (code is in Scala)
As a former GWT developer, I've always intended to take a look a Vaadin, so that would seem 
a good choice for this (current, is Java, builds to war).  Vaadin provides a lot of goodness out-of-the-box,
such as x-site surfing attack prevents, but it is however a server-side framework, so need to consider server
memory usage per-user.

## Design
Factoring logic across a number of sub-project/modules:

* webui - will be the initial user interface to the application
* model - implements domain concepts with in this BC
* service - business logic that doesn't fit domain objects (less intrinsic)
* common - D.R.Y. helpers

### Persist / service
Stores and retrieve task and user info, exposes interface for clients
As business logic minimal at current stage, decided to provide service interface out of the persistence
module, as the prime functionality is store/retrieve; if requirements were to grow, this should
be split into two modules, with service providing the interface used by the UI, and being a client
of the persist module.   
Additionally, both of these modules might benefit from a separate *-api  module containing the public 
interface specification, if we envisage the need to provide alternate persist strategies and/or service
implementations (this play nicely with OSGi, too)

### Technology
Underlying DB is HSQLDB, easy to configure, run in-memory for testing and persist to
disk for production
Persistence provided via a mapper interface, using MyBatis: simple to configure and tune, does not
pollute domain model with persistence concerns; storage strategy based around the Aggregate Root pattern,
with no direct linkage between entity types in the database (use GUIDs as synthetic keys): this allows
a modular approach to be maintained - no FK constraints, also benefits performance/scalability.
Assume minimal container support (e.g, no JNDI) when configuring data source

### Why certain approaches were used, why others were not used
My approach to structuring the application is based on my usual technique for building for OSGi: single-purpose
modules (bundles), that are cohesive and loosely-coupled, having a limited and well-defined interface.  Even 
linked into one app, this is still a useful approach, meaning individuals modules can be designed and tested
as stand-alone modules.  This also facilitates swapping-out, e.g., the Party services could be replaced
by a third-party solution, such as Shiro and/or LDAP.
Persistence has been kept out of the domain model, where such concerns are not appropriate, and kept behind 
a persistence service interface.  This approach is informed by recent experience with the DDD repository model,
and would facilitate 'modular databases' - e.g., some stuff might be better stored in an RDMS (for queries, say),
which others may benefit from MongoDB, etc.  

### Any design patterns you used 
On the back-end, Dependency Injection is a prime pattern, using Spring; Module pattern is used extensively,
to enforce separation of concerns and manage dependencies.
Rather than Null Object (which gets tedious to maintain), I used the Option (monad) pattern to represent
successful or failed operations (called Optional in this app).
Within the service / persistence module, the Strategy/Bridge pattern is employed, enabling simpler testing via
mocks but also leaving the design open to alternate persistence implementations (not that I was gold-plating, tho...).
The persistence interface itself exhibits the Mapper pattern, the implicit design of iBatis/MyBatis.
UI is being re-factored in the direction of MVP, disconnecting the presentation logic from the 
view (display controls.  The model is served-up via the injected service interface.

### Anything extra you would have done given more time
Learn more about Vaadin: re-factored more, build a testing framework, based on mocking VaadinRequest
Plenty more functionality and usability improvements, which would have made the service module richer,
and worth splitting out from the persistence model. 
Useful functionality might include:
- Notifications (email, sms)
- To-be-done-by dates
- Task categorization
- Re-occurring tasks 

### Anything else you feel I should know

Full Disclosure...
The UI page was 'lifted' from a Vaadin tutorial example (see https://vaadin.com/tutorial), 
and re-factored extensively.

## Release Notes
* User Acceptance Criteria : all satisfied
* Unit test coverage: 95%+ : 100% for server-side modules, 95% for Web UI
* i18n ready : UI internationalized
* Appropriate error handling : exceptions contained in module, logged; Vaadin default error notification acceptable
* Operations logging : appropriate warnings suitable for Oper console
* Performance : Ran against JMeter, memory usage c. 2MB per user, would need action before production
* Documentation (design / release notes) : this document plus full Javadoc

Performance: Main risk is many users loading up large to-do lists in memory, leading to server performance degradation due
to GC cycles or OOM failure.
Vaadin runs all UI logic on the server-side and user interaction in the client-side UI, and this can result in
a large number of request-response round trips to the server. 
Vaadin keeps controls and data is 'session-locale', nice for concurrency, but initial probes show a memory
footprint of 2 .. 5MB per user, depending upon number of tasks and activity.
This can be managed by modifying the table model / service interface to page data, or simply to limit the max number of open tasks  

Concurrent update policy: optimistic, last man wins as data is access is all per-user Id (could constrain user to single session) 

### Internationalization
UI text is abstracted to a resource bundle, English and German provided (login as roy/pa55sw0rd  for German locale)

### Test Environments
* Tomcat (versions: 6.0.29 as maven plugin, 7.0.19 server)
* Browsers: Chrome 28.0, Firefox 20.0.1, Internet Explorer 8.0
* url: http://localhost:8080/cedar-webui/ (or as per Tomcat deployment / war file name)


## Test Coverage Report

```
mvn cobertura:cobertura -Dcobertura.aggregate=true -Dcobertura.report.format=html
```

### Status
[![Build Status](https://travis-ci.org/sothach/cedar.png)](https://travis-ci.org/sothach/cedar)
[![Coverage Status](https://coveralls.io/repos/github/sothach/cedar/badge.svg?branch=master)](https://coveralls.io/github/sothach/cedar?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c76d1333d7f7444394c4cd49def96e8d)](https://www.codacy.com/project/sothach/cedar/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sothach/cedar&amp;utm_campaign=Badge_Grade_Dashboard)


