# Website Project structure

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

## Page structure
All html pages will be formed by the layout template in `themes/CarbonLDP/layout/layout.ejs` using the following layouts and partial layouts. :

|layouts call by all pages|function|
|-------------------------|--------|
|`themes/CarbonLDP/layout`|directory that holds layouts|
|_partial/head.ejs|defines metadata, always imports main.css, and other css files if `themes/_head/<layout-name-head>.`|
|_partial/header.ejs|generates the page header and navigation menu, using the data in `theme.nav` defined in `themes/config.yml`|
|<layout>.ejs|calls for corresponding layout defined in front-matter of `source/<page-name>/index.ejs`|
|_partial/footer.ejs|contains de template of the footer|
|_partial/after-footer.ejs|always imports scripts: jquery, semantic ui, widgets, main.js, and other scripts if defined in front-matter of `source/<page-name>/index.ejs`|

## Page styles
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

### styles file structure
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



## Page scripts
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

### scripts file structure
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

## Page content

### Principal pages
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

### Documentation pages

Pages that hold CarbonLDP's product documentation. It uses three different layouts: version, category, document.
From the data files in `source/_data/documentation.yml` and using the version and category layouts we build:
	- 1 general landing page configured to show the version of the documentation with the label *current* configured in themes/CarbonLDp/config.yml
	- 1 landing page per version
	- 1 landing page per category

The documentation is generated from an independent source file for each document.

For more information please refer to documentation-structure.md
    

### Images

All images are stored in `themes/CarbonLDP/`

## Tools available

Collection of helpers, widgets and partial layouts that implement automated functions. e.g.: sidebar, tabs, etc

For more information please refer to widgets-and-tools.md

