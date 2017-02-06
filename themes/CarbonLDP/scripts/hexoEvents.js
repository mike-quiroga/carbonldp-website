hexo.on(( "generateBefore" ), function(){
    
    var pagesObj = hexo.locals.get("pages");
    var pages = JSON.stringify( pagesObj );
});