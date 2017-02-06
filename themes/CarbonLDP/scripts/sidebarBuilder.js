hexo.extend.helper.register( "sidebarBuilder", function( pageContent ) {

    var sidebarContent = '';
    var sidebar = '<div class="ui sticky segment"><h2 class="ui header">Content</h2><div class="ui vertical following fluid accordion text menu">';
    var mobileSidebar = '<div class="ui mobile only grid"><div class="row"><div class="ui segment"><h2 class="ui header">Content</h2><div class="ui vertical following fluid accordion menu">';

    // Start of jsdom-global, virtual DOM access
    var cleanup = require("jsdom-global")(pageContent)
    var allSections = document.querySelectorAll("section");

    for (var i = 0; i < allSections.length; i++) {
        if (allSections[i].parentNode === document.body) {
            var sections = allSections[i].querySelectorAll(".ui.header");
            if (typeof sections !== "undefined") {
                if (sections.length - 1 === 0) {
                    sidebarContent += '<div class="item"><a>' + sections[0].textContent + '</a></div>';
                } else {

                    sidebarContent += '<div class="item"><a>' + sections[0].textContent +
                        '</a><i class="dropdown icon"></i><div class="content menu">';
                    for (var j = 1; j < sections.length; j++) {
                        sidebarContent += '<a class="item" >' + sections[j].textContent + '</a>';

                    }

                    sidebarContent += '</div></div>';
                }
            }
        }
    }

    sidebar += sidebarContent + '</div></div>';
    mobileSidebar += sidebarContent + '</div></div></div></div';
    allSections[0].insertAdjacentHTML('beforebegin', mobileSidebar);

    pageContent = document.body.outerHTML + '</div></div></div><div class="four wide computer only four wide tablet only column"><div class="sidebar">' + sidebar + '</div></div></div></div>'

    cleanup();
    // End of virtual DOM access

    return pageContent;

});