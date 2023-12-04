import ServiceMessageModel from '/js/ServiceMessageModel.js';
import ServiceInfoModel from "/js/ServiceInfoModel.js";

$(document).ready(function() {
    $("#registerForm").submit(function (event) {
        event.preventDefault();
        var getServiceString = "http://localhost:8080/disc"

        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();
        var fullName = $("input[name='fullName']").val();
        var email = $("input[name='email']").val();

        // get the user register service url
        async function fetchServiceUrl() {
            try {
                let response = await $.ajax({
                    url: getServiceString,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        code: ServiceMessageModel.SERVICE_DISCOVER_REQUEST,
                        data: ServiceInfoModel.SERVICE_USER_SAVE
                    }),
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

        // send the register request
        async function registerReq() {
            let requestURL = await fetchServiceUrl();
            console.log(requestURL);
            console.log(fullName);
            $.ajax({
                url: requestURL,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    username: username,
                    password: password,
                    fullName: fullName,
                    email: email
                }),
                success: function(response) {
                    console.log(response)
                    if (response.userID === -1){
                        alert("Register Failed");
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
        registerReq()
    });
    // if user cancel it
    $("#cancelRegister").click(function(event){
        event.preventDefault();
        window.location.href = '/login.html';
    });
})