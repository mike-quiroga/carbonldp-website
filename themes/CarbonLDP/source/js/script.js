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

    verticalMenu.toggle();

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

})(jQuery);