(function() {

	// Get-started form validation
	// Revise the validation
	var form = {
		html: document.getElementById( "icpsignup" ),
		nameInput: document.getElementById( "icpsignupName" ),
		emailInput: document.getElementById( "icpsignupEmail" ),
		button: document.getElementById( "icpsignupSubmitButton" )
	};

	form.errorMessage = form.html.querySelector( ".error.message" );


	// add event listeners for inputs and form

	form.html.addEventListener( "submit", onSubmit );
	form.nameInput.addEventListener( "input", validateName );
	form.nameInput.addEventListener( "blur", validateName );


	form.emailInput.addEventListener( "input", validateEmail );
	form.emailInput.addEventListener( "blur", validateEmail );

	function onSubmit( $event ) {
		form.html.action = "https://app.icontact.com/icp/core/mycontacts/signup/designer/form/?id=63&cid=581321&lid=23554";
		form.html.submit();
	}

	function validateName( e ) {
		var errors = form.errorMessage.querySelectorAll( ".name" );
		
		for( var i = 0, length = errors.length; i < length; i++ ){
			errors[i].remove();
		}

		var input = (e.srcElement||e.target);
		var field = input.parentNode;

		if( input.value ) {
			field.classList.remove( "error" );
			checkForErrorsMessage( form.emailInput );
		} else {
			field.classList.add( "error" );
			form.errorMessage.style.display = "block";
			form.button.disabled = true;
			var li = document.createElement( "li" );
			li.classList.add( "name" );
			var textNode =  document.createTextNode("Please enter your name" );
			li.appendChild( textNode);
			form.errorMessage.querySelector( "ul" ).appendChild( li );
		}

	}

	function validateEmail( e ) {
		var errors = form.errorMessage.querySelectorAll( ".email" );

		for( var i = 0, length = errors.length; i < length; i++ ){
			errors[i].remove();
		}


		// Get email field htmlElement
		var input = (e.srcElement||e.target);
		var field = input.parentNode;

		// Create li element to add error messages
		var li = document.createElement( "li" );
		li.classList.add( "email" );

		

		// Check for input
		if( input.value ) {
			var re = /\S+@\S+\.\S+/;

			//Check if input is email
			if( ! re.test( input.value ) ) {

				// Add error class to field
				field.classList.add( "error" );
				form.errorMessage.style.display = "block";
				form.button.disabled = true;
				var textNode =  document.createTextNode("Please enter a valid email" );
				li.appendChild( textNode);
				form.errorMessage.querySelector( "ul" ).appendChild( li );
			} else {
				field.classList.remove( "error" );
				checkForErrorsMessage( form.nameInput );
			}
		} else {
			field.classList.add( "error" );
			form.button.disabled = true;
			form.errorMessage.style.display = "block";
			var textNode =  document.createTextNode("Please enter your email" );
			li.appendChild( textNode);
			form.errorMessage.querySelector( "ul" ).appendChild( li );
		}
	}

	function checkForErrorsMessage( field ) {
		if( ! (form.errorMessage.querySelectorAll( "li" ).length > 0) ) {
			if( field.value ) form.button.disabled = false;
			form.errorMessage.style.display = "none";
		}
	}
})();