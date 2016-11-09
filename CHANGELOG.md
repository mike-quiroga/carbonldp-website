# 0.11.1 (2016/11/09)

- Changed CMD statement to ENTRYPOINT in docker image to allow arguments

# 0.11.0 (2016/11/09)

- Completed #50 - Document JS SDK authentication
- Completed #101 - Add back to top button in website for mobile view
- Completed #99 - Add required fields and email validation on register-form in get started
- Created a NodeJS based server to replace the current one. This server will be fed with the route table to generate metadata tags properly
- Added dynamic generation of `sitemap.xml` through the new server and route table
- Fixed #107 - Mobile rendering
    - Added viewport metatag to index.ejs
    - Added sidebar for mobile view in community and support page
    - Modified website styles to render correctly on mobile view


# 0.10.0 (2016/11/01) :skull:

- Completed #53 - Document how to upload and download files
- Completed #97 - Disable app-dev module in website
- Completed #98 - Update links in JS SDK getting started
- Completed #102 - Disable all app-dev references from website.routing, and all documents
- Removed unnecessary code to scroll top from website, there is an implemented fix in app.component

# 0.9.0 (2016/10/27)

- Completed #54 - Review and refine JS SDK > Object Schema documentation
- Completed #90 - Implement new about page design  
- Completed #94 - Add content menu for mobile view in all documents
- Completed #82 - Improve mobile menu
    - Fixed mobile view styles
    - Modified documentation menu to close when a selection is made
- Fixed #93 - Inconsistent layout for previous and next on mobile sizing
    - Fixed styles in documentation.view.scss for  "ui steps" class
    - Added column width for documents content in computer, tablet and mobile views

# 0.8.1 (2016/10/26)

- Fixed gzip on proxied requests

# 0.8.0 (2016/10/25)

- Completed #56 - Review and finalize Object Model document in the JavaScript SDK documentation
- Completed #78 - Add components for pending documents
- Complete #88 standardize documentation styles and messages
- Modified first icon for gitter rooms in community and support view
- Modified abstract content in community and support view
- Modified rdf-source.html to show not yet documented message
- Modified header labels for REST API
- Standardized different aspects of documentation pages: 
    - Abstract message box
    - Breadcrumbs
    - Not yet documented message
- Completed #55 - Review and refine JS SDK > Contexts documentation
- Improved performance by enabling gzip and minifying prod files

# 0.7.0 (2016/10/17)

- Added success message after registration on quick start guide page
- Added Quick Start to documentation home
- Changed JS SDK and REST API order on header menu, and documentation home
- Modified About page for BETA release
- Fixed log in on tablet view
- Removed App-Dev from documentation home
- Removed facebook link on footer
- Completed #59, Create Get Started registration page and add to primary navigation.
- Added final example to JS-SDK Getting Started page
- Completed #60, Create Community & Support page and add to primary navigation
- Completed [LDP-790](https://jira.base22.com/browse/LDP-790) Add meta-tag.service
- Added description to routes
- Modified JavaScript SDK getting started document examples
- Update branch to angular2.0.2
- Completed #58, Write Quick Start Guide and add to navigation menu
- Upgraded to angular 2.0.2
- Completed #62, Header and footer components use reserved HTML elements as selector
- Completed #61, Create license page (in router, but not linked to by anything yet; found at `/license` or `/src/license`.
- Completed #47, Finish the About page sufficient for Beta release and move out it from under Documentation and into the primary nav bar.

# 0.6.0 (2016/08/10)

- Add complete initial draft JavaScript SDK Object Schema.
- Add new documentation landing page.
- Add styles for new documentation landing page.
- Add new icon images.
- Add HighlightDirective in documentation.
- Complete initial draft JavaScript SDK Object Model.
- Complete initial draft JavaScript SDK Getting Started.
- Fix router links to documentation in documentation landing page.
- Modify header documentation menu.
- Modify example comments in JS SDK documentation.

# 0.5.18 (July 14, 2016)

- Fix @angular package version to rc.1
- Remove the following from App Dev:
  - Animated carbon logo component.
  - Unused modal component.
  - SPARQLClient component.
  - ValidationService.
  - jstree from node_dependencies on packages.json
  - CodeMirrorComponent.
  - LoginComponent.
  - Highlight directive.
  - AppContextService.
  - App component and view.
  - Apps-list component.
  - Create-app component.
  - My-apps-sidebar service.
  - HeaderComponent.
  - Sidebar component.
- Change styles of all components and views of AppDev to use :host.
- Change all the providers to use the ones from carbon panel.
- Rename files of AppDevLoginView to align with code style.
- Remove unusued imports and variables from all AppDev components.
- Change MyApps component to use the whole component from carbon panel.

# 0.5.17 (July 11, 2016)

- Add partial draft JavaScript SDK Object Model.
- Add global styles for documentation.
- Complete draft JavaScript SDK Contexts.
- Content improvements/corrections to Getting Started with the REST API.
- Fix WebsiteView to scroll to Top on route change.
- Combine documents into documentation.

# 0.5.16 (June 8, 2016)

- Fix bug with SPARQL Editor that hides the initial h from http when displaying results.
- Fix my-apps action buttons to redirect to proper route.
- Add auth ticket to URL when trying to download backups to BackupsService and BackupsListComponent.
- Add errorMessage when the creation of the document download URL fails.
- Add jstree as a devDependency in node
- Add to PropertyValueComponent new read and edit modes.
- Add document edition functionality to Document Explorer, so that you can do the following:
  - Document:
  	- Create:
      - Properties
    - Delete:
      - Properties
    - Read:
      - Properties
      - Blank Nodes
      - Named Fragments
    - Edit:
      - Properties
      - Blank Nodes
      - Named Fragments
  - Property:
  	- Create:
      - Literals (@value, @type and @language)
      - Pointers (@id)
  	- Delete:
      - Literals (@value, @type and @language)
      - Pointers (@id)
    - Read:
      - @id
      - @type
      - Literals (@value, @type and @language)
      - Pointers (@id)
      - Any custom properties (E.g: http://example.org/ns#PropertyName)
    - Edit:
      - Literals (@value, @type and @language)
      - Pointers (@id)
      - Any custom properties (E.g: http://example.org/ns#PropertyName)
- Changed DocumentsResolverService to refresh ETags whenever a document is being fetched.
- Add new components:
  - BlankNodeComponent (To display a blank node)
  - NamedFragmentComponent (To display a named fragment)
  - LiteralComponent (To display the contents of a property literal)
  - LiteralsComponent (To display the literals of a property)
  - PointerComponent (To display the contents of a pointer literal)
  - PointersComponent (To display the pointers of a property)
- Rename DocumentExplorer components to follow angular's 2 naming convention:
  - Documents Resolver Service
    -  DocumentsResolverService.ts -> document-resolver.service.ts
  - Document Explorer
    -  DocumentExplorerComponent .ts-> document-explorer.component.ts
  - Document Tree View
    -  DocumentTreeViewComponent.ts -> document-tree-view.component.ts
  - Document Viewer
    -  DocumentViewerComponent.ts -> document-viewer.component.ts
  - Blank Nodes
    -  BNodeComponent.ts -> blank-node.component.ts
    -  BNodesComponent.ts -> blank-nodes.component.ts
  - Named Fragments
    -  NamedFragmentComponent.ts -> named-fragment.component.ts
    -  NamedFragmentsComponent.ts -> named-fragments.component.ts
  - Document Resource
    -  DocumentResourceViewerComponent.ts -> document-resource.component.ts
  - Property
    -  PropertyComponent.ts -> property.component.ts
  - List Viewer
    -  ListViewerComponent.ts -> list-viewer.component.ts
  - Lists
    -  LiteralComponent.ts -> literal.component.ts
    -  LiteralsComponent.ts -> literals.component.ts
  - Pointers
    -  PointerComponent.ts -> pointer.component.ts
    -  PointersComponent.ts -> pointers.component.ts

# 0.5.15 (June 1, 2016)
- Implement new design for Home page.
- Content improvements/corrections to Getting Started with the REST API.
- Complete initial draft of REST API doc for RDFSource.

# 0.5.14 (Jun 1, 2016)
- Add app configuration page.
- Add App Configuration component.
- Add BackupsComponent to handle fetch, exports and imports of backups.
- Add ExportBackupComponent to generate a backup.
- Add ImportBackupComponent to import backups from URL's, existing backups or uploading a backup file.
- Add ListBackupsComponent to list, download and remove backups.
- Add BackupsService to get, getAll, create and delete a backup.
- Add JobsService to get, getAll, create, run job and check a job execution.
- Add Jobs model class.
- Add ErrorMessageComponent to ErrorsAreaComponent to display messages in a single <error-message> tag.
- Remove Edit option from opened App sidebar options.
- Add app configuration option to App sidebar options.
- Add copy_node_modules:files and copy_node_modules:packages tasks to gulpfile to copy files and packages from node_modules.
- Fix DocumentTreeViewComponent to import JStree CSS from assets/node_modules and inject it into the document head on the ngOnInit phase of the component.
- Fix bug with SPARQL Editor that hides the initial h from http when displaying results. Also remove unused imports from SPARQLEditorComponent.
- Fix my-apps action buttons to redirect to proper route.

# 0.5.13 (May 17, 2016)
- Add named fragments component to the DocumentViewer of DocumentExplorer.
- Remove tabs that showed raw data on each property of a document.
- Emphasize the slug of the @id property.
- Make document viewer to scroll when changing documents.
- Change document viewer sections to tabs.
- Change generateMaps of bNodes and namedFragments to use RDFDocument Util class methods
- Remove Edit option from opened App sidebar options.

# 0.5.12 (May 11, 2016)
- Add app explorer view.
- Add new DocumentExplorerComponent with the following components:
	- DocumentTreeView: to list all the children of a document/app.
	- DocumentViewer: to display the content/properties of a document.
	- PropertyComponent: to display a property of a Document.
	- bNodeViewerComponent: to display bNodes of a document.
	- DocumentsResolverService: to fetch documents context.
	- ListViewerComponent: to display lists when embeded in properties.
- Add link to App Explorer in sidebar.
- Fix general AppDev layout padding and margins to display footer correctly.
- Fix jspm carbonldp file-type issue with a mapping.

# 0.5.11 (April 28, 2016)
- Add a list view to MyApps.
- Add search to MyApps view
- Add new App tile design.
- Add action buttons to Apps tiles and Apps list.
- Add loading indicator to AppsListView.
- Add sorting to AppsList on table view.
- Reorganize my apps child views and components structure.
- Change AppDev App class to use only a slug and an App.Context
- Add app action buttons component.
- Add CreateApp and EditApp routes to my apps

# 0.5.10 (April 28, 2016)
- Add EditApp view and EditApp form component to AppDev.

# 0.5.9 (April 14, 2016)
- Add google analytics feature with angulartics2 plug in, and customized event tracking
- Add newsletter component
- Add routerOnActivate on all methods to set Title.
- Create signup-thanks PageView
- Fix styles in footer, header, home Components.
- Hide routes not ready for Prod. in Header Component.
- Add angulartics2 typings

# 0.5.8 (April 14, 2016)
- Add form to create a new App.
- Change AppContextService functions get and getAll to use the new getContext and getAllContexts functions from the Carbon Javascript SDK.
- Add link to create app page in My Apps.

# 0.5.7 (April 13, 2016)
- Add protocolAndHost field to GettingStarted TypeScript so that documentation can always reflect the server you're on.
- Upgrade to Angular beta 14
- Add Eclipse .project file to .gitignore.
- Change order of dependencies to avoid ["Uncaught (in promise)TypeError: object is not a constructor"](https://github.com/blacksonic/angular2-es6-starter/issues/1).
- Remove documentation of dependency on global packages.
- Modify Getting Started with the REST API documentation sufficient to start reviews with Alpha testers.
- Add debug config option to enableProdMode in Angular
- Replace code-mirror read only instances with highlight.js, it's much faster and highly optimized for read only syntax highlighting
- Make npm scripts cross platform compatible (by removing relative directories)
- Update code-mirror so it works with ng-content and fixes extra tabs

# 0.5.6 (March 28, 2016)
- Add logout option to AppDev.
- Add rememberMe option to loginComponent.
- Add login view for AppDev to AppComponent.
- Add link to AppDevLogin on mobile view.
- Fix login component error messages layout.
- Fix login component displaying outside viewport on small screens.
- Fix minor tslint errors.
- Fix AppDev header menu misbehaviour on mobile screens.
- Update Angular2 to beta 13
- Replace live-server with gulp-webserver

# 0.5.5 (March 16, 2016)
- Add confirmation when changing between queries.
- Change SPARQL Editor UI.
- Change Carbon context to dev instead of local.
- Add errors area component to App-Dev template.
- Add section to SPARQL Editor to display breaking errors.
- Add option to SPARQL to emit errors.
- Add service to the ErrorsArea to send errors to errorsArea.
- Fix SPARQL onReExecute response.
- Fix SPARQL onConfigure response.
- Change object clonation method to use ES6 Object.assign on SPARQL Editor.
- Change SPARQL Editor onClickSavedQuery algorithm.
- Add method to display errors [ 400, 403, 404, 413, 414, 429 ] in the response stack.
- Add accept headers when sending DESCRIBE and CONSTRUCT queries.
- Add clean query button.
- Add success or failure icon on responses stack when ASK queries return true or false.
- Add SPARQL query to each response on the response stack.
- Add confirmation to delete saved queries.
- Add more formats to output formats (ntriples, trix, trig, binary, nquads, rdfa).

# 0.5.4 (March 16, 2016)
- Added authentication to access AppDev.
- Added js-cookie library.
- Added cookies to store Carbon.Auth.Credential tokens.
- Added Carbon is authenticated validation while trying to access any component of AppDev.

# 0.5.3 (March 14, 2016)
- Added sidebar component for documents.
- Added styles for documents and sidebar components.
- Added documents home index.
- Change documents pages to angular components.
- Fix typo in Getting started with rest api document.
- Removed ContentComponent and ContentService.
- Removed documents from assets.

# 0.5.2 (March 14, 2016)
- Updated JSPM to 0.17
- Added tsconfig.json and restructured files to use it

# 0.5.1 (March 4, 2016)
- Updated Getting started with rest api document

# 0.5.0 (March 4, 2016)
- Updated Angular to Beta 8.
- Added a login view.
- Added a color palette to the UI with 7 new colors.
- Added an Application Development module with:
    - A service to retrieve applications.
    - A view to list retrieved applications.
    - A navigation bar for breadcrumbs.
    - A sidebar to provide navigation throughout the module.
    - Integration with the SPARQL Query Editor on an App context.

# 0.4.0 (March 3, 2016)
- Switched tsd to typings
- Added angular2 and carbon definition files (they provide jspm import support while we wait for WEB-18904)

# 0.3.2 (February 29, 2016)
- Fixed DI. static private parameters property shouldn't be needed anymore.

# 0.3.1 (February 15, 2016)
- Fixed relative paths issue in different environments
- Added configuration file and support for ejs templates for index.html, boot.ts and any scss
- Any gulp task can now be executed with a -p or --profile argument to specify environment (local|dev|prod)
   
   Example: `gulp build --profile dev`

# 0.3.0 (February 3, 2016)
- Update Angular2 to Beta 3
- Fix getting started indentation

# 0.2.0 (December 9, 2015)
- Added a new **SPARQL Query Editor** using Semantic UI Framework.
- Fixed SPARQL-Client to work with scss files instead of css.
- Changed dynamic logo to static image.
- Added CodeMirror to **SPARQL Query Editor** responses.
- Fixed Semantic UI version.
- Fixed build gulp task and fix dist/ starting path
- Added on configure query, add re execute query functionality.
- Refactored SPARQLClientComponent classes.
- Removed ResultsetComponent.

# 0.1.0 (November 6, 2015)
- Initial release
