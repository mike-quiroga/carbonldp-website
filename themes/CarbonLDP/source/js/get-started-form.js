// Get-started form validation
// Revise the validation
let form = {};
	form.html = document.getElementById("icpsignup");
	form.nameInput = document.getElementById("icpsignupName");
	form.emailInput = document.getElementById("icpsignupEmail");
    form.errorMessage = form.html.querySelector(".error.message");
    form.button = document.getElementById("icpsignupSubmitButton");

	// add event listeners for inputs and form

	form.html.addEventListener( 'submit', onSubmit );
	form.nameInput.addEventListener('input', validateName);
	form.nameInput.addEventListener('blur', validateName);


	form.emailInput.addEventListener('input', validateEmail);
	form.emailInput.addEventListener('blur', validateEmail);

function onSubmit( $event ){
	form.html.action = "https://app.icontact.com/icp/core/mycontacts/signup/designer/form/?id=63&cid=581321&lid=23554";
	form.html.submit();
}

function validateName( e ){
	let errors = form.errorMessage.querySelectorAll(".name");
	errors.forEach( function( error ) {
		error.remove();
	});

	let input = e.srcElement;
	let field = e.srcElement.parentNode;

	if( input.value){
		field.classList.remove("error");
		checkForErrorsMessage( form.emailInput );
	} else{
		field.classList.add("error");
		form.errorMessage.style.display = "block";
		form.button.disabled = true;
		let li = document.createElement("li");
		li.classList.add("name");
		li.append("Please enter your name");
		form.errorMessage.querySelector("ul").append( li );
	}

}

function validateEmail( e ){
	let errors = form.errorMessage.querySelectorAll(".email");
	errors.forEach( function( error ) {
		error.remove( );
	});

	// Get email field htmlElement
	let input = e.srcElement;
	let field = e.srcElement.parentNode;

	// Create li element to add error messages
	let li = document.createElement("li");
	li.classList.add("email");

	// Check for input
	if( input.value){
		let re = /\S+@\S+\.\S+/;

		//Check if input is email
		if( !re.test( input.value ) ) {

			// Add error class to field
			field.classList.add( "error" );
			form.errorMessage.style.display = "block";
			form.button.disabled = true;
			li.append("Please enter a valid email");
			form.errorMessage.querySelector("ul").append( li );
		} else {
			field.classList.remove( "error" );
			checkForErrorsMessage( form.nameInput );
		}
	} else{
		field.classList.add("error");
		form.button.disabled = true;
		form.errorMessage.style.display = "block";
		li.append("Please enter your email");
		form.errorMessage.querySelector("ul").append( li );
	}
}

function checkForErrorsMessage( field ){
	if( ! (form.errorMessage.querySelectorAll( "li" ).length > 0) ) {
		if( field.value) form.button.disabled = false;
		form.errorMessage.style.display = "none";
	}
}



