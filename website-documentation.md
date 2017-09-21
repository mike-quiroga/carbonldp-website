# Website documentation

## How the pages are rendered?
All the pages in carbonldp.com are build through hexo.js

Hexo.js is a fast, simple & powerful blog framework, that allows us to create static pages through the use of layout templates, source files and other tools.

All the layouts in `themes/CarbonLDP/layout` are called through layout.ejs that renders:
 - header.js
 - other layouts: these layouts can render other partial layouts.
 - footer.js

## Edit carbonldp.com content

### Edit the home page

The home page is rendered by the home.ejs partial layout that is called by the index.ejs layout

 1. Go to `themes/CarbonLDP/layout/_partial/`
 2. Edit the file `home.ejs`

### Edit the header
The header is rendered in all the pages of the site by the header.ejs partial layout that is called by the layout.ejs

1. Go to `themes/CarbonLDP/layout_partial/`
2. Edit the file `header.ejs`

### Edit the footer
The footer is rendered in all the pages of the site by the footer.ejs partial layout that is called by the layout.ejs

1. Go to `themes/CarbonLDP/layout/ _partial/`
2. Edit the file `footer.ejs`

## Edit principal pages
A principal page, is a page that can be found at the top level of the navigation menu. All principal pages have their own layout.

In the `source/` directory, the first level of directeroy are principal pages, inside each directory you can find a `index.ejs` file. Inside this file in the front matter the layout used by the principal page is defined, the layout used will be named after the directory the file is in. 
```
	---
	layout: layout-name, from the available layouts in themes/CarbonLDP/layout
	title: page title, will be used to create metatags for SEO
	description: small description, will be used to create metatags for SEO
	---
```

1. Go to `themes/CarbonLDP/layout`
2. Search for the correspondent `.ejs` layout file and edit it.

### Edit documentation


## Add content to carbonldp.com
### Add a principal page

A principal page, is a page that can be found at the top level of the navigation menu

1. Add the directory and file in source/
There are two options to creating the new page:
    a. Manually:
        - Create a new directory in `source/` and create a
        - Create a file .ejs in the new directory with the name "index"

    b. In the terminal run the command `hexo new page "directory-name"`
        - Rename "index.md" file to change extension "index.ejs"
        "
    - naming the directory: the name of the directory will be part of the link of the publish site
    - hexo will create the page using the page scaffold
2. Edit index.ejs" front-matter
	front-matter: block of YAML or JSON at the beginning of the file that is used to configure settings for your writings.[hexo - front-matter documentation](https://hexo.io/docs/front-matter.html)

	- Add this three settings, they are mandatory.
	```
	---
	layout: layout-name, from the available layouts in themes/CarbonLDP/layout
	title: page title, will be used to create metatags for SEO
	description: small description, will be used to create metatags for SEO
	---
	```

	Other optional settings:
	cover: will display an image as a cover or banner, as seen in carbonldp.com/blog
	script: will add a script to this page in the published site.
	```
	---
	cover:
    	image: image-name.extension, image to be used
    	alt: description to be added to the image
    script: script-name, script in themes/CarbonLDP/source/assets/scripts
	---
	```

### Add link to page in navigation menu

To show the page in the navigation menu follow this steps:

1. Go to themes/config.yml
2. Locate the nav variable under the #Header comment
3. Add the page with the following information:
    - Add this information in the order you wish it to appear in the navigation bar (left - right)
	- If there are children directories you can add up to two sub directories using the key "children"
```
- name: directory name
   path: directory path
   children: children directories or null
```
or
```
- name: directory name
   url: external url
```

e.g.:
```
- name: Documentation
  path: /documentation
  children:
	- name: Quick Start Guide
	  path: /quick-start-guide
	  children: null
	- name: Essential Concepts
	  path: /essential-concepts
	  children: null
	- name: JavaScript SDK
	  path: /javascript-sdk
	  children:
	    - name: Guide
	      path: /getting-started
	      children: null
	    - name: API Reference
	      url: https://carbonldp.github.io/carbonldp-js-sdk/
	      children: null
```
## Add head for the principal page
The head files contains the styles that will be used by this particular page

All principal pages have their own layout in themes/CarbonLDP/layout

1. Add a ".ejs" file in themes/CarbonLDP/layout
    - name the file with the same name of the directory of the page, followed by -head
       e.g: directoryName-head.ejs
2. Add css helper to head layout;
	<%- css("/assets/styles/css/css-style-name")%>

## Add content to principal page

All principal pages have their own layout in themes/CarbonLDP/layout

1. Add a ".ejs" file in themes/CarbonLDP/layout
    - name the file with the same name of the directory of the page
2. Add html content to display.

- In the layout you have access to:
     - front-matter settings of the index.md that uses the layout through the variable "page"
     - theme settings in config.yml through "theme"
     - data stored in file through "data["file-name]"
     - helpers in themes/CarbonLDP/scripts through <%-helper-file-name(...)%>
     - all assets in source/assets that will be used in the published site.
