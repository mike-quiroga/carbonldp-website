# carbonldp-website

[![Build Status](https://travis-ci.org/CarbonLDP/carbonldp-website.svg)](https://travis-ci.org/CarbonLDP/carbonldp-website)

Source code for [carbonldp.com](https://carbonldp.com)

## Development 

### Setup

1. Install dependencies
    - [node.js](https://nodejs.org/en/)
    - gulp: `npm install gulp -g`
    - hexo: `npm install -g hexo-cli`
2. run `npm install`
3. run `gulp build`
4. run `hexo server` to serve the static site (the actual generated site goes in carbonldp-website/public)

### Create a new blog post

1. run `hexo new post post-file-name`, a new file will be added to source/_posts folder
2. In the front-matter (https://hexo.io/docs/front-matter.html) of the post file change or add:
	- title
	- author (optional)
	- avatar (optional) add the image to themes/source/assets/images
3. Write your content in markdown ( https://guides.github.com/features/mastering-markdown/ )
4. Add an excerpt, place a comment `<!-- more -->` the post excerpt will be from the beginning to where the comment was place.

### File Structure

TODO: Describe each file/directory

    .
    ├── .idea                               
    ├── build
    ├── hooks                       
    ├── node_modules                        
    ├── public - files of generated site
    ├── scaffolds - files used by Hexo as template to build files in the source using the command "hexo new scaffold-name"
    ├── server                        
    ├── source - source files of the content to generate files and directory ithe public site
    │    ├── _data - files containing raw data used to generate landing pages, available layout - "data["file-name"]"
    │    ├── _post - all posts published on carbonldp.com/blog, available themes/CarbonLDP/layout - "post.variable-name"
    │    ├── about
    │    ├── blog
    │    ├── community-and-support
    │    ├── documentation - all directories for versions of all the CarbonLDP documentation files
    │    ├── get-started
    │    └── license
    ├── themes
    │    ├── CarbonLDP
    │    │    ├── layout - template files used to generate the public site
    │    │    ├── scripts - helpers that can be used in the layout
    │    │    ├── semantic - semantic ui files
    │    │    ├── source
    │	 │    │	   └── assets - files used to generate public/assets directory
    │    │    │         ├── images - images used in the public site
    │    │    │         ├── scripts - scripts used in public site
    │    │    │         ├── semantic - semantic ui files
    │    │    │         └── styles - styles used in public site
    │    │    ├── widgets - scripts and styles of widgets used by hexo-widgets package to generate widgets in public site.
    │    │    └── config.yml - configuration file holds variables, available in layout - "theme.variable-name"
    │	 └── landscape - hexo default theme
    ├── .gitignore 
    ├── .travis.yml
    ├── _config.yml - general hexo configuration file
    ├── CHANGELOG.md
    ├── db.json
    ├── Dockerfile
    ├── gulpfile.js
    ├── LICENSE
    ├── package.json
    ├── package-lock.json
    ├── README.md
    └── semantic.json
    
### Contributing

- Please read [our conventions and GitHub workflow](https://github.com/CarbonLDP/carbonldp/wiki/GitHub-conventions-and-workflow-for-Carbon-LDP)
