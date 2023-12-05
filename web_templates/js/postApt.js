import ServiceMessageModel from '/js/ServiceMessageModel.js';
import ServiceInfoModel from "/js/ServiceInfoModel.js";

$(document).ready(function() {
    $("#postAptForm").submit(function (event) {
        event.preventDefault();
        let storedUserData = localStorage.getItem('user');
        console.log(1111)
        if (!storedUserData) {
            alert("Please Login First!");
        } else {
            let userObject = JSON.parse(storedUserData);

            var getServiceString = "http://localhost:8080/disc"

            var aptName = $("input[name='aptName']").val();
            var address = $("input[name='address']").val();
            var price = $("input[name='price']").val();
            var type = $('#type').val();
            var area = $("input[name='area']").val();
            var availableDate = $("input[name='availableDate']").val().toString();
            var descr = $('#descr').val();
            console.log(availableDate)
            console.log(descr)

            // get the user register service url
            async function fetchServiceUrl() {
                try {
                    let response = await $.ajax({
                        url: getServiceString,
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            code: ServiceMessageModel.SERVICE_DISCOVER_REQUEST,
                            data: ServiceInfoModel.SERVICE_APT_SAVE
                        }),
                    });
                    if (response.code === 202) {
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
            async function postAptReq() {
                let requestURL = await fetchServiceUrl();
                console.log(requestURL);
                $.ajax({
                    url: requestURL,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        aptName: aptName,
                        address: address,
                        area: area,
                        price: price,
                        availableDate: availableDate,
                        Type: type,
                        Descr: descr
                    }),
                    success: function (response) {
                        console.log(response)
                        alert("Post Success!");
                        window.location.href = '/index.html';
                    },
                    error: function (error) {
                        alert("Post Apartment Failed");
                        console.log(error)
                    }
                });
            }

            postAptReq()
        }
    });

    // if user cancel it
    $("#cancelPost").click(function(event){
        event.preventDefault();
        window.location.href = '/index.html';
    });
})