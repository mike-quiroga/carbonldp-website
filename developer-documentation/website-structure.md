
A detailed file structure of the entire project can be found [README.md](https://github.com/CarbonLDP/carbonldp-website/blob/develop/README.md)

All website pages are build by hexo using different files in this project:
- `source/` files
- `scripts/` files
- `themes/CarbonLDP/layout/` files:
    - `_head` files
    - `_partial layouts` files
    - `layout` files
- `themes/CarbonLDP/source/assets/` files:
    - `images` files
    - `scripts` files
    - `semantic` files
    - `styles` files
- `source/_data` files

We use hexo to generate our site, to know more about how hexo works please refer to their [documentation](https://hexo.io/docs/)

After the pages are processed by hexo a package hexo-widgets further processes the pages to build the public site adding widgets defined by the user

Hexo Widgets
- `themes/CarbonLDP/widgets/` files

# Page structure
All html pages will be formed by the layout template in `themes/CarbonLDP/layout/layout.ejs` using the following layouts and partial layouts. :

|layouts call by all pages|function|
|-------------------------|--------|
|`themes/CarbonLDP/layout`|directory that holds layouts|
|_partial/head.ejs|defines metadata, always imports main.css, and other css files if `themes/_head/<layout-name-head>.`|
|_partial/header.ejs|generates the page header and navigation menu, using the data in `theme.nav` defined in `themes/config.yml`|
|<layout>.ejs|calls for corresponding layout defined in front-matter of `source/<page-name>/index.ejs`|
|_partial/footer.ejs|contains de template of the footer|
|_partial/after-footer.ejs|always imports scripts: jquery, semantic ui, widgets, main.js, and other scripts if defined in front-matter of `source/<page-name>/index.ejs`|

# Page styles
Styles are defined in `themes/CarbonLDP/source/assets/styles/`. The css styles are built from the scss files defined by the user, and are imported by the partial layout `themes/CarbonLDP/layout/_partial/head.js`

The styles of the website are closely related to the layouts in `themes/CarbonLDP/layout` directory

There are two general stylesheet that affect the entire website, and that are always iported by `head.js`
- main.css : general style that will be reflected in the entire website. BE CAREFUL when adding or deleting styles, you may be affecting areas you are unaware.
- [semantic ui](https://semantic-ui.com/)

Other styles imported:
- widget styles, go to widgets sections to know more about this

You can add specific styles to a particular layout:
1. Add a file in `themes/CarbonLDP/layout/head`, the file must be named `<layout-name>-head.ejs`
2. In the file `themes/CarbonLDP/layout/head/<layout-name>-head.ejs`
    - Add helper to import styles `<%-css("assets/styles/css/<css-file-name>)`, the file to be imported should be in `themes/CarbonLDP/source/assets/styles/`

## styles file structure
    .
    ├── themes
    │    ├── CarbonLDP
    │    │    ├── layout - template files used to generate the public site
    │    │    │     ├── head - helpers that can be used in the layout
    │    │    │     │    └── <layout-name>-head.ejs
    │    │    │     └── <layout-name>.ejs
    │    │    ├── source
    │    │    │     └── assets
    │    │    │         └── styles
    │    │    │              ├── scss
    │    │    │              │   └── <style-file-name>.scss
    │    │    │              └── css
    │    │    │                  └── <style-file-name>.css  (this file is generated from the scss file, run `gulp build`)



# Page scripts
Scripts are defined in `themes/CarbonLDP/source/assets/scripts/` and are imported by the partial layout `themes/CarbonLDP/layout/_partial/after-footer.js`

There is one general script that affects the entire website, and that is always iported by `after-footer.js`
- main.js : general script that will be reflected in the entire website. BE CAREFUL when adding or deleting anything from this file you may be affecting areas you are unaware.

Other scripts imported by default
- [semantic ui](https://semantic-ui.com/)
- [jQuery](https://jquery.com/)
- widget scripts, go to widgets sections to know more about this

You can add specific sscripts to a particular page:
1. Add `<page-name>.js` file in `themes/CarbonLDP/source/assets/scripts/`
2. Go to the page source `source/<page-name>/index.ejs`
    - Add `script: <page-name>` in page front-matter.

There are other styles imported such as widget styles, got to widgets sections to know more about this

## scripts file structure
    .
    ├── source
    │    └── <page-name>
    │         └── index.ejs
    ├── themes
    │    ├── CarbonLDP
    │    │    ├── source
    │    │    │     └── assets
    │    │    │         └── scripts
    │    │    │              └── <page-name>.js

# Page content

## Principal pages
The content of all pages present in the navigation menu and in the first level of directories in `source/` directory is defined in the layouts of the same name in `themes/CarbonLDP/layout`. Some of this pages may have defined particular `head` layouts and may use partial layouts and specific scripts as well.

|Principal Pages|Layouts * -> head layouts ** |Partial layouts|Scripts *** |
|---------------|:----------------------|---------------|-------|
|`source/`|`layout/`|`layout/_partial`|`source/assets/scripts`|
|about/index.ejs|about.ejs -> _head/about-head.ejs| blog-sidebar.ejs, pots.ejs|
|blog/index.ejs|blog.ejs -> _head/blog.ejs|
|community-and-support/index.ejs|community-and-support.ejs|
|get-started/index.ejs| get-started.ejs -> _head/get-started.ejs| |get-started.js|
|license/index.ejs| license.ejs|
|index.ejs|index.ejs|_partial/home.ejs|

\* All layout templates are called from the `layout.ejs` where the head, header, footer and after-footer are also called

\** All pages use a general `head.ejs` file that can be found in `themes/CarbonLDP/layout/_partial`

\*** All pages use a general `main.js` file that can be found in `themes/CarbonLDP/source/assets/scripts`

**front-matter**

|front-matter variables|mandatory|
|----------------------|---------|
|layout|yes|
|title|yes|
|description|yes|
|cover|no|

## Documentation pages

The documentation pages contain all sorts of guides and information that helps the final user to understand and use the full potential of Carbon LDP. The documentation is formed by three types of pages: version, category and the documentation itself. 

These types of pages are generated by the following layouts:

- **Version**: general landing pages of the documentation. They enlist all the categories that exist in a given version
By default the version with the label current will be linked directly in the header of carbonldp.com
- **Category**: Categories are landing pages specific to a category, the enlist all the documents that exist in a given version
- **Document**: The final document. Individually written in `source/documentation/`

Both **version** and **category** content is obtained from the data file `source/_data/documentation.yml` and are autogenerated using their respective layouts, to know about the structure of the data file go to section [**documentation.yml**](https://github.com/CarbonLDP/carbonldp-website/blob/task/%23161_website-developer-documentation/developer-documentation/website-structure.md#documentationyml).

|Pages|Layouts | head layouts |
|-----|--------|--------------|
|`source/documentation`|`layout/documentation`|`layout/_head/documentation`|
|index.ejs|version.ejs | version-head.ejs|
|`/vx.x.x/index.ejs`|version.ejs | version-head.ejs|
|`/vx.x.x/category1/index.ejs`|category.ejs|category-head.ejs|
|`/vx.x.x/category1/document1/index.ejs`|document.ejs|dcument-head.ejs|

**front-matter**

The front-matter of these pages contain the usual information listed in principal pages section, but it may also contain:

|front-matter variables|Description|
|----------------------|-----------|
|version:vx.x.x|madatory, it tells you to which version the document, cateogry, version landing pages it belongs|
|sidebar:disabled|disables sidebar|
|notDocumented:true|activates not-yet-documented partial layout|

**Partial layouts**

Partial layouts allow you to add cetain features to the pages, specific to the documentation are the layouts listed below

|Partial layouts|Use|
|---------------|---|
|`/layout/documentation/_partial`|
|edit-document.ejs|add link to document in github|
|not-yet-documented.ejs| replace all content in a document with a pre existing layout with a sorry-not-yet-documented message, to activate it add `notDocumented: true` in the front-matter of tha page|
|versions-menu.ejs|adds a menu with all the versions that exist for the documentation|

### documentation.yml

Documentation.yml is a file found in `source/_data`, it contains the all the data related to the versions of the documentation, the categories and documents per version. This file allows the project to automatically generate the content of the version and category landing pages, including the content of the versions menu.

To add a new version simply add an object in versions object, the object must have this values:

```
name: vx.x.x
label: name of the version
categories:
  - title: name of the category
    description: description of the category
    link: slug to build the path to the category
    iconPath: assets/images/<category-icon-name>
    documents:                                  // optional - if there are documents
      - title: document title                        
        description: document description
	link: slugk to build the link
    relatedLinks:                               // optional - if there are documents or external url
      - title: external document/website name
        description: document/external document description
        link: external url
	
```

e.g.

```
versions:
  - name: v0.x.x
    label: current  
    categories:
      - title: Quick Start
        description: Start now!
        link: quick-start-guide
        iconPath: assets/images/ico-open-book.svg
      - title: JavaScript SDK
        description: description of the category
        link: javascript-sdk
        iconPath: assets/images/ico-javascript.svg
        documents:
          - title: Getting Started with the JavaScript SDK
            link: getting-started
            description: description of the document
        relatedLinks:
          - title: API reference
            link: https://carbonldp.github.io/carbonldp-js-sdk
            description: Classes and methods available in the JavaScript SDK, API reference maintained on Github
  - name: v1.0.x
    label: alpha
    categories:
      - title: Platform
        description: The main component of the Carbon suite
        link: platform
        iconPath: assets/images/ico-motor.svg
        documents:
          - title: Startup arguments
            link: startup
            description: Reference for all arguments accepted by the platform upon startup
      - title: JavaScript SDK
        description: Description of the category.
        link: javascript-sdk
        iconPath: assets/images/ico-javascript.svg
        documents:
          - title: Getting started
            link: getting-started
            description: Guide to install the SDK and start creating and manipulating data with it
```
**curent label** the label in the versions give each version a more friendly name, however it alse serves another purpose, the label: current is assigned to the documentation that reflects the most stable version of CarbonLDP. The version with the current label value is assigned as the default version that will be linked in carbonldp.com nvaigation menu.

### documentation file structure
    .
    ├── source
    │    ├── _data
    │    │    └── documentation.yml
    │    ├── documentation
    │    │    ├── vx.x.x
    │    │    │    ├── category1     
    │    │    │    │    ├── document1 
    │    │    │    │    │    └── index.ejs
    │    │    │    │    ├── document2 
    │    │    │    │    │    └── index.ejs
    │    │    │    │    └── index.ejs
    │    │    │    ├── category2    
    │    │    │    │    ├── document1 
    │    │    │    │    │    └── index.ejs
    │    │    │    │    ├── document2 
    │    │    │    │    │    └── index.ejs
    │    │    │    │    └── index.ejs
    │    │    │    └── index.ejs
    │    │    └── index.ejs
    

## Images

All images are stored in `themes/CarbonLDP/`

# Tools avalible 

## Helpers and partial layouts

Helpers are functions that can be use in the layout templates, Hexo has [predefined helpers](https://hexo.io/docs/helpers.html) that you can use  or you can define your own helpers in `themes/CarbonLDP/scripts`

- **breadcrumb.js and breadcrumb.ejs**

	Returns the elements needed to form a breadcrumb from the path of the page where it is being called.
	The partial helper imports a partial layout `breadcrumb.ejs` renders a breadcrumb using the `breacrumb` helper to build it.

	The partial layout `breadcrumb.ejs` recieves the boolean parameter: `shortBreadcrumb`. 
  	  - false: will render a breadcrumb including all slugs that form the path
  	  - true: will render a breadcrumb that includes only the first and last slug from the path.
    
	**e.g.**

	```
	//page path: /documentation/v0.x.x/quick-start-guide/

	<%- partial(_partial/breadcrumb, {shortBreadcrumb: false})%>

	//resulting breadCrumb: Documentation > V0.x.x -> Quick Start Guide

	<%- partial(_partial/breadcrumb, {shortBreadcrumb: true})%>

	//resulting breadCrumb: Documentation -> Quick Start Guide

	```

- **inline_css.js**

	Returns an inline minify style that is inserted where the helpers was called.

	**e.g.**

	```
	//themes/CarbonLDP/layout/_partial/head.ejs. line 43
	<%- inline_css("assets/styles/css/main.css")%>
	```

- **svg.js**

	Returns the content of a file inside a div. Used to read the content of svg vector images

	**e.g.**

	```
	// <%- svg("filePath/fileName.extension", "classToUseInDIV")
	`<%- svg("/assets/images/ico-rest-api.svg", "ui small centered circular image")%>`

	//Result
	`<div class="ui small centered circular image"> content of ico-rest-api.svg </div>`
	```

## hexo-widgets

[hexo-widgets](https://github.com/MiguelAraCo/hexo-widgets) package, allows you to create custom widgets that can be used while writing new posts/pages/anything. hexo-widgets must be imported in the project by npm.

hexo-widgets registers the helper render-widgets, that allows you to further process an html using widgets defined in `themes/CarbonLDP/widgets`, it also add scripts and styles in the after-footer and head respectively.

This helper is used in `themes/CarbonLDP/layout/layout.ejs`

e.g.

`<%- render-widgets(htmlToProcess, page )%>`

- **async-background**
	Adds a script to load a background image asynchronously when attribute "data-sync-background" is defined

	**Basic widget structure**

	```
	<div data-async-backgroun="filepath"></div>
	```

	**e.g.** 

	```
	// night-sky.jpg will be loaded asynchronously
	<div class="cover cover--home" data-async-background="/assets/images/night-sky.jpg"></div>
	```

- **highlight**
	Applies highlight.js to all elements inside `pre > code`
	Adds inline styles:
	    -  highlight.js/styles/tomorrow-night.css,
		-  highlight.css"

- **sidebar**

	Creates a two level deep sidebar menu that serves as an index of the document, only visible in computer, tablet view. 

	It uses the content attribute to define the content from which the sidebar will be created, and generates the sidebar using the HTML tags <section> and header tags to obtain the titles of the sections of the document. It also adds styles and scripts for the sidebar.

	**Basic widget structure**

	```
	//<div class="mainContent"> everything in here will be processed to build the sidebar </div>
	//<sidebar content="mainContent"> the sidebar will be rendered here </sidebar>
	```

	**e.g**

	```
	<div class="mainContent">
	    <section><h1>First Section</h1>
		<section><h2>Subsection</h2></section>
	    </section>
	    <section><h1>Second Section</h1></section>
	    <section><h1>Third Section</h1></section>
	</div>

	<sidebar content="mainContent></sidebar>
	```

	**Resulting html with sidebar**

	```
	<div class="mainContent">
	    <section id="first-section"><h1>First Section</h1>
		<section id="subsection-title"><h2>Subsection Title</h2></section>
	    </section>
	    <section id="second-section"><h1>Second Section</h1></section>
	    <section id="third-section><h1>Third Section</h1></section>
	</div>

	<div class="sidebar" content="mainContent">
		<div class="ui sticky segment">
			<h2 class="ui header">Content</h2>
			<div class="ui vertical following fluid accordion text menu sidebar-verticalMenu">
		    <div class="item">
			<a class="title sidebar-title sidebar-title--dropdown" href="#first-section">First Section</a>
			<i class="dropdown icon sidebar-dropdownIcon"></i>
					<div class="content menu sidebar-verticalSubMenu">
						<a class="item sidebar-title" href="#subsection-title">Subsection Title</a>
					</div>
				</div>
		    <div class="item"><a class ="title sidebar-title" href="#second-section">Second Section</a></div>
		    <div class="item"><a class ="title sidebar-title" href="#third-section">Third Section</a></div>
		</div>
	    </div>
	</div>
	```

- **staticContentMenu**
	As the sidebar widget, the staticContentMenu widget creates a two level deep static menu that serves as an index of a document. 

	It uses the content attribute to define the content from which the menu will be created, and generates it using the HTML tags <section> and header tags to obtain the titles of the sections of the document. It also adds necessary styles and scripts.

	This widget is used directly in the source file, it will appear wherever the tags `<staticContentMenu>` is inserted and is tipically is used in our documetns to create an index for mobile view.

	**Basic widget structure**

	```
	<div class="mainContent"> everything in here will be processed to build the sidebar </div>
	<staticContentMenu content="mainContent"> the sidebar will be rendered here </staticContentMenu>
	```

	**e.g.**

	```
	<div class="mainContent">
	    <section><h1>First Section</h1>
		<section><h2>Subsection</h2></section>
	    </section>
	    <section><h1>Second Section</h1></section>
	    <section><h1>Third Section</h1></section>
	</div>
	<div class="ui mobile only grid">
		<div class="row">
			<staticContentMenu content="mainContent"></staticContentMenu>
		</div>
	</div>
	```

	**Resulting html with staticMenu**

	```
	<div class="mainContent">
	    <section id="first-section"><h1>First Section</h1>
		<section id="subsection-title"><h2>Subsection Title</h2></section>
	    </section>
	    <section id="second-section"><h1>Second Section</h1></section>
	    <section id="third-section><h1>Third Section</h1></section>
	</div>

	<div class="ui mobile only grid">
		<div class="row">
		<div class=" sixteen wide mobile only column">
		    <div class="staticContentMenu">
			<h2 class="staticContentMenuTitle">Content</h2>
			<div class="ui vertical accordion fluid menu mobile">
			    <div class="item">
			    <a class="title staticContentMenu-title staticContentMenu-title--dropdown" href="#first-section">
				First Section</a>
			    <i class="dropdown icon .staticContentMenu-dropdownIcon"></i>
				<div class="content menu">
				    <a class="item" href="#subsection-title">Subsection Title<</a>
				</div>
			    </div>
			    <div class="item"><a class ="title staticContentMenu-title" href="#second-section">Second Section</a></a></div>
			    <div class="item"><a class ="title staticContentMenu-title" href="#third-section">Third Section</a></div>
			</div>
		    </div>
		</div>
	    </div>
	</div>
	```

- **tabs**

	Process tabs selector to create a tabs widgets for computer and mobile views. 
	In computer and tablet view:
	    - It will create a menu with tabs for each `<tab>` selector found in `<tabs>` 
	    - The title attribute is tha name that will be displayed in each tab. 
	    - The name attribute is the data-tab or tab id use to identify each tab if it is not provided a random id will be generated.

	![tabs in comouter/tablet view](https://github.com/CarbonLDP/carbonldp-website/blob/task/%23161_website-developer-documentation/developer-documentation/images/tabs-computer-tablet.png)

	In mobile view:
	    - It will create a selection menu, for each `<tab>` in `<tabs>` an option will be found in the menu.
	    - The title attribute is the name that will be displayed in each tab. 
	    - The name attribute is the data-tab or tab id use to identify each tab if it is not provided a random id will be generated.

	![tabs in mobile view](https://github.com/CarbonLDP/carbonldp-website/blob/task/%23161_website-developer-documentation/developer-documentation/images/tabs-mobile.png)

	**Basic widget structure**

	```
	<tabs>
		<tab title="tab Title">
				//content of the tab
		</tab>
		<tab title="tab Title" name="tab-id">
		//content of the tab
	    </tab>
	</tabs>
	```

	**e.g**

	```
	<tabs>
			<tab title="TypeScript" name="typescript">
				<pre><code class="typescript">
					Code example in typescript
				</code></pre>
			</tab>
			<tab title="JavaScript ES2015">
				<pre><code class="javascript">
					Code example in javascript
				</code></pre>
			</tab>
			<tab title="JavaScript ES5">
				<pre><code class="javascript">
					code example in javascript-es5
				</code></pre>
			</tab>
		</tabs>

	```

	**Resulting tabs html**

	```
	<div class="ui grid tabsComponent">
	    <div class="ui computer tablet only row tabs">
			<div class="ui top attached tabular menu tabs-titles">
		    <a class="tabs-title active item" data-tab="typescript">TypeScript</a>
				<a class="tabs-title  item" data-tab="VQYRvJBt">JavaScript ES2015</a>
				<a class="tabs-title  item" data-tab="SmuwDqty">JavaScript ES5</a>
		</div>
	    </div>
	    <div class="ui mobile only row tabs">
		<div class="ui compact selection dropdown tabs-options">
		    <i class="dropdown icon"></i>
		    <div class="text">TypeScript</div>
		    <div class="menu">
			<div class="tabs-option item" data-tab="typescript">TypeScript</div>
			<div class="tabs-option item" data-tab="VQYRvJBt">JavaScript ES2015</div>
			<div class="tabs-option item" data-tab="SmuwDqty">JavaScript ES5</div>	
		    </div>
		</div>
	    </div>
	    <div class="tabs-tab ui bottom attached tabs active tab segment" data-tab="typescript">
			<pre>
		    <code class="typescript">
			    Code example in typescript
		    </code>
		</pre>
		</div>
	    <div class="tabs-tab ui bottom attached tabs  tab segment" data-tab="VQYRvJBt">
			<pre>
		    <code class="javascript">
			Code example in javascript
		    </code>
		</pre>
	    </div>
		<div class="tabs-tab ui bottom attached tabs  tab segment" data-tab="SmuwDqty">
			<pre>
		    <code class="javascript">
			code example in javascript-es5
		    </code>
		</pre>
	    </div>
	</div>
	```

