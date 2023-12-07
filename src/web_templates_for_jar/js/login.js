import ServiceMessageModel from '/js/ServiceMessageModel.js';
import ServiceInfoModel from "/js/ServiceInfoModel.js";

$(document).ready(function(){
    $('#wishListLink').click(function (event) {
        alert("Please Login First")
        event.preventDefault()
    })
    $("#loginForm").submit(function(event){
        event.preventDefault();
        var getServiceString = "http://localhost:8080/disc"

        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();

        //request message to get the load service
        let loadServiceReqMsg =
            new ServiceMessageModel(ServiceMessageModel.SERVICE_DISCOVER_REQUEST,
            ServiceInfoModel.SERVICE_USER_LOGIN);

        // get the service url
        async function fetchServiceData() {
            try {
                let response = await $.ajax({
                    url: getServiceString,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(loadServiceReqMsg),
                });
                if (response.code === 202){
                    alert("Service Not Found");
                } else {
                    var serviceData = JSON.parse(response.data);
                    return serviceData.serviceHostAddress;
                }
            } catch (error) {
                console.log(error);
            }
        }

        // send the login request
        async function handleData() {
            let requestURL = await fetchServiceData();
            console.log(requestURL);
            $.ajax({
                url: requestURL,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    username: username,
                    password: password
                }),
                success: function(response) {
                    if (response.userID === -1){
                        alert("Username or Password is not correct");
                    } else {
                        console.log(response)
                        localStorage.setItem('user', JSON.stringify(response));
                        window.location.href = '/index.html';
                    }
                },
                error: function(error) {
                    alert("Username or Password is not correct");
                    console.log(error)
                }
            });
        }

        handleData();

    });

    $("#goToRegister").click(function(event){
        event.preventDefault();
        window.location.href = '/register.html';
    });
});
