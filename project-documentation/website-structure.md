# Website pages structure

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

Ater the pages are processed by hexo a package hexo-widgets further processes the pages to build the public site

Hexo Widgets
- `themes/CarbonLDP/widgets/` files

### Page structure
The html page will be formed with:

|layouts call by all pages|function|
|-------------------------|--------|
|`themes/CarbonLDP/layout`|directory that holds layouts|
|_partial/head.ejs|defines metadata, always imports main.css, and other css files if `themes/_head/<layout-name-head>.`|
|_partial/header.ejs|generates the page header and navigation menu, using the data in `theme.nav` defined in `themes/config.yml`|
|layout.ejs|calls for correspondet layout defined in front-matter of `source/<page-name>/index.ejs`|
|_partial/footer.ejs|contains de template of the footer|
|_partial/after-footer.ejs|always imports scripts: jquery, semantic ui, widgets, main.js, and other scripts if defined in front-matter of `source/<page-name>/index.ejs`|

## Page styles
Styles are defined in `themes/CarbonLDP/source/assets/styles/` and are imported by the partial layout `themes/CarbonLDP/layout/_partial/head.js`

The styles of the website are closely related to the layouts in `themes/CarbonLDP/layout` directory

There are two general stylesheet that affect the entire website, and that are always iported by `head.js`
- main.css : general style that will be reflected in the entire website. BE CAREFUL when adding or deleting styles, you may be affecting area you are unaware.
- [semantic ui](https://semantic-ui.com/)

Other styles imported:
- widget styles, go to widgets sections to know more about this

You can add specific styles to a particular layout:
1. Add a file in `themes/CarbonLDP/layout/head`, the file must be named `<layout-name>.ejs`
2. In the file `themes/CarbonLDP/layout/head/<layout-name>.ejs`
    - Add `<%-css("assets/styles/css/<css-file-name>)`, file should be in `themes/CarbonLDP/source/assets/styles/`

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
    │    │    │              └── scss
    │    │    │              │   └── <style-file-name>.scss
    │    │    │              └── css
    │    │    │                  └── <style-file-name>.css  (this file is generated from the scss file, run `gulp build`)



## Page scripts
Scripts are defined in `themes/CarbonLDP/source/assets/scripts/` and are imported by the partial layout `themes/CarbonLDP/layout/_partial/after-footer.js`

There is one general script that affects the entire website, and that is always iported by `after-footer.js`
- main.js : general script that will be reflected in the entire website. BE CAREFUL when adding or deleting anything from this file you may be affecting area you are unaware.

Other scripts imported by default
- [semantic ui](https://semantic-ui.com/)
- [jQuery](https://jquery.com/)
- widget scripts, go to widgets sections to know more about this

You can add specific sscripts to a particular page:
1. Add `<page-name>.js` file in `themes/CarbonLDP/source/assets/scripts/`
2. Go to the page source `source/<page-name>/index.ejs`
    - Add `script: <page-name>` in page front-matter.

There are other styles imported such as widget styles, got to widgets sections to know more about this

### styles file structure
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

### Principal pages and documentation landing pages
The content of all pages present in the navigation menu and in the first level of directories in `source/` directory is defined in the layouts of the same name in `themes/CarbonLDP/layout`


|Principal Pages|Layouts -> other layouts|
|---------------|-------|
|`source/`|`themes/CarbonLDP/layout/`|
|about/index.ejs| about.ejs -> _head/about-head.ejs|
|blog/index.ejs|blog.ejs -> _head/blog.ejs|
|community-and-support/index.ejs|community-and-support.ejs|
|documentation/index.ejs|documentation-home.ejs -> _head/documentation-home.ejs|
|documentation/v.0.x.x/index.ejs|documentation-home.ejs -> _head/documentation-home.ejs|
|documentation/v1.0.x/index.ejs|documentation-home.ejs -> _head/documentation-home.ejs|
|get-started/index.ejs| get-started.ejs -> _head/get-started.ejs|
|license/index.ejs| license.ejs|
|index.ejs|_partial/home.ejs|


### Documents





