$(document).ready(function(){
    $("form").submit(function(event){
        event.preventDefault();

        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();

        $.ajax({
            url: '/login',
            type: 'post',
            data: {
                username: username,
                password: password
            },
            success: function(response){
                console.log(response);
            },
            error: function(xhr, status, error){
                console.log(error);
            }
        });
    });
});
