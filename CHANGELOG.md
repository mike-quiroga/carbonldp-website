# 0.5.4 (March 16, 3016)
* Added authentication to access AppDev.
* Added js-cookie library.
* Added cookies to store Carbon.Auth.Credential tokens.
* Added Carbon is authenticated validation while trying to access any component of AppDev.

# 0.5.3 (March 14, 3016)
* Added sidebar component for documents.
* Added styles for documents and sidebar components.
* Added documents home index.
* Change documents pages to angular components.
* Fix typo in Getting started with rest api document.
* Removed ContentComponent and ContentService.
* Removed documents from assets.

# 0.5.2 (March 14, 2016)
* Updated JSPM to 0.17
* Added tsconfig.json and restructured files to use it

# 0.5.1 (March 4, 2016)
* Updated Getting started with rest api document

# 0.5.0 (March 4, 2016)
* Updated Angular to Beta 8.
* Added a login view.
* Added a color palette to the UI with 7 new colors.
* Added an Application Development module with:
    *  A service to retrieve applications.
    *  A view to list retrieved applications.
    *  A navigation bar for breadcrumbs.
    *  A sidebar to provide navigation throughout the module.
    *  Integration with the SPARQL Query Editor on an App context.

# 0.4.0 (March 3, 2016)
* Switched tsd to typings
* Added angular2 and carbon definition files (they provide jspm import support while we wait for WEB-18904)

# 0.3.2 (February 29, 2016)
* Fixed DI. static private parameters property shouldn't be needed anymore.

# 0.3.1 (February 15, 2016)
* Fixed relative paths issue in different environments
* Added configuration file and support for ejs templates for index.html, boot.ts and any scss
* Any gulp task can now be executed with a -p or --profile argument to specify environment (local|dev|prod)
   
   Example: `gulp build --profile dev`

# 0.3.0 (February 3, 2016)
* Update Angular2 to Beta 3
* Fix getting started indentation

# 0.2.0 (December 9, 2015)
* Added a new **SPARQL Query Editor** using Semantic UI Framework.
* Fixed SPARQL-Client to work with scss files instead of css.
* Changed dynamic logo to static image.
* Added CodeMirror to **SPARQL Query Editor** responses.
* Fixed Semantic UI version.
* Fixed build gulp task and fix dist/ starting path	
* Added on configure query, add re execute query functionality.
* Refactored SPARQLClientComponent classes.
* Removed ResultsetComponent.

# 0.1.0 (November 6, 2015)
* Initial release
