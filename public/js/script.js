(function($) {

    //header js
   $(".navMenu-dropdownButton").dropdown({
        on: "hover",
        action: "hide",
   });

    $(".navMenu-dropdownButton--mobile").dropdown();
    
    var verticalMenu = $(".navMenu-verticalMenu");

    $(".navMenu-openerButton").on("click", function (e) {
        e.preventDefault();
        verticalMenu.toggle();
    });

    // verticalMenu.toggle();

    // Activates scroll with offset 
    $( ".sidebar .menu a[href], .categoriesMenu-button" ).on( "click", scrollTo );
    
    // Scroll to selected section or subsection in the article
    function scrollTo( event ) {
        let id = $( event.currentTarget ).attr( "href" ).replace( "#", "" );
        let $element = $( "#" + id );
        let position = $element.offset().top - 100;

        $element.addClass( "active" );

        $( "html, body" ).animate( {
            scrollTop: position
        }, 500 );
        location.hash = "#" + id;
        event.stopImmediatePropagation();
        event.preventDefault();

        return false;
    }
  
    //get-started form validation

    $(".registerForm--getStarted").form({
        on: "change",
        fields: {
            name: {
                identifier: "data[carbon-full-name]",
                rules: [
                    {
                        type   : "empty",
                        // prompt : "Please enter your name"
                    }
                ]
            },
            email: {
                identifier: "data[email]",
                rules: [
                    {
                        type   : "empty",
                        // prompt : "Please enter your email address"
                    },
                    {
                        type : "email",
                        // prompt : "Please enter a valid email address"
                    }
                ]
            }
        }
    });

    
    // generate a Random String

    function randomToken(){
        let tokenElements = document.querySelectorAll(".js-generateRandomToken")

        for (i = 0, length = tokenElements.length; i < length; ++i) {
            let randomToken = randomString( 32, "aA#");
            tokenElements[i].innerHTML = randomToken;
        }
        
        return true;
    }

    function randomString( length, chars ){

        let mask = "";
        if( chars.indexOf( "a" ) > - 1 ) mask += "abcdefghijklmnopqrstuvwxyz";
        if( chars.indexOf( "A" ) > - 1 ) mask += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if( chars.indexOf( "#" ) > - 1 ) mask += "0123456789";
        if( chars.indexOf( "!" ) > - 1 ) mask += "~`!@#$%^&*()_+-={}[]:\";'<>?,./|\\";

        let result = "";
        for ( let i = length; i > 0; -- i ) result += mask[ Math.floor( Math.random() * mask.length ) ];

        return result;
    };
    
    
    //activate accordions
    $(".ui.accordion").accordion();



    // //activate all tabs
    // $( ".tabs-titles > .tabs-title, .tabs-options .tabs-option").on("click", changeAllTabs);
    //
    // function changeAllTabs(){
    //     let allTabsComponents = document.querySelectorAll("tabsComponent");
    //     console.log(allTabsComponents);
    // };





    randomToken();

})(jQuery);
