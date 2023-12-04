$(document).ready(function() {
    let storedUserData = localStorage.getItem('user');
    if (storedUserData) {
        let userObject = JSON.parse(storedUserData);
        $('#loginLink').hide(); // hide the login link
        $('#logoutLink').show(); // show the logout button
        $('#userDisplay').text(userObject.fullName).show(); // show the usename

        // logout
        $('#logoutLink').click(function() {
            localStorage.clear(); // clean local storage
            location.reload();
        });
    } else {
        $('#loginLink').show();
        $('#logoutLink').hide();
        $('#userDisplay').hide();
    }
});

