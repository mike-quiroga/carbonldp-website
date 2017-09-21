## How the pages are rendered?
All the pages in carbonldp.com are build through hexo.js

Hexo.js is a fast, simple & powerful blog framework, that allows us to create static pages through the use of layout templates, source files and other tools.

All the layouts in `themes/CarbonLDP/layout` are called through layout.ejs that renders:
 - header.js
 - other layouts: these layouts can render other partial layouts.
 - footer.js