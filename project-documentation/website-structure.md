# Website structure

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

Ater the pages are processed by hexo a package hexo-widgets further processes the pages to build the public site adding widgets defined by the user

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
    │    │    │              └── scss
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

## styles file structure
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

## Documentation pages

Restructured by Miguel Aragon in PR #162

# Tools avalible 

## Helpers and partial layouts

Helpers are functions that can be use in the layout templates, Hexo has [predefined helpers](https://hexo.io/docs/helpers.html) that you can use  or you can define your own helpers in `themes/CarbonLDP/scripts`

### breadcrumb.js and breadcrumb.ejs

Returns the elements needed to form a breadcrumb from the path of the page where it is being called.
The partial helper imports a partial layout `breadcrumb.ejs` renders a breadcrumb using the `breacrumb` helper to build it.

The partial layout `breadcrumb.ejs` recieves the boolean parameter: `shortBreadcrumb`. 
    - false: will render a breadcrumb including all slugs that form the path
    - true: will render a breadcrumb that includes only the first and last slug from the path.
    
e.g.
```
//page path: /documentation/v0.x.x/quick-start-guide/

<%- partial(_partial/breadcrumb, {shortBreadcrumb: false})%>

//resulting breadCrumb: Documentation > V0.x.x -> Quick Start Guide

<%- partial(_partial/breadcrumb, {shortBreadcrumb: true})%>

//resulting breadCrumb: Documentation -> Quick Start Guide

```

### inline_css.js
Returns an inline minify style that is inserted where the helpers was called.

e.g.
```
//themes/CarbonLDP/layout/_partial/head.ejs. line 43
<%- inline_css("assets/styles/css/main.css")%>
```

### svg.js

Returns the content of a file inside a div. Used to read the content of svg vector images

e.g.

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

**e.g.**

`<%- render-widgets(htmlToProcess, page )%>`

### async-background
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

### highlight
Applies highlight.js to all elements inside `pre > code`
Adds inline styles:
    -  highlight.js/styles/tomorrow-night.css,
	-  highlight.css"

### sidebar
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

### staticContentMenu
It works the same as the sidebar widget, but it creates a static menu from the content it process. Only visible in mobile view

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

### tabs

Process tabs selector to create a tabs widgets for computer and mobile views. 
In computer and tablet view:
    - It will create a menu with tabs for each `<tab>` selector found in `<tabs>` 
    - The title attribute is tha name that will be displayed in each tab. 
    - The name attribute is the data-tab or tab id use to identify each tab if it is not provided a random id will be generated.

In mobile view:
    - It will create a selection menu, for each `<tab>` in `<tabs>` an option will be found in the menu.
    - The title attribute is tha name that will be displayed in each tab. 
    - The name attribute is the data-tab or tab id use to identify each tab if it is not provided a random id will be generated.

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

