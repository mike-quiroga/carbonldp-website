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

### Create a post

1. run `hexo new post post-file-name`, a new file wil be added to source/_posts folder
2. In the front-matter (https://hexo.io/docs/front-matter.html) of the post file change or add:
	- title
	- author (optional)
	- avatar (optional) add the image to themes/source/assets/images
3. Write your content in markdown ( https://guides.github.com/features/mastering-markdown/ )
4. Add an excerpt, place a comment `<!-- more -->` the post excerpt will be from the beginning to where the comment was place.

