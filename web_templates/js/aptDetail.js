import ServiceMessageModel from '/js/ServiceMessageModel.js';
import ServiceInfoModel from "/js/ServiceInfoModel.js";

$(document).ready(function() {
    let storedUserData = localStorage.getItem('user');
    if (storedUserData) {
        let userObject = JSON.parse(storedUserData);
        $('#loginLink').hide(); // hide the login link
        $('#logoutLink').show(); // show the logout button
        $('#userDisplay').text(userObject.fullName).show(); // show the usename

        // logout
        $('#logoutLink').click(function () {
            localStorage.clear(); // clean local storage
            location.reload();
        });
    }

    $('#wishListLink').click(function (event) {
        if (!storedUserData) {
            alert("Please Login First")
            event.preventDefault()
        }
    })

    let selectedAptId = localStorage.getItem('selectedApartmentId');
    if (selectedAptId) {
        fetchAptById(selectedAptId)
    }
    $("#addWishListForm").submit(function (event) {
        event.preventDefault();
        let storedUserData = localStorage.getItem('user');
        if (!storedUserData) {
            alert("Please Login First!");
        } else {
            let userObject = JSON.parse(storedUserData);
            addApt2WishList(selectedAptId, userObject.userID)
        }
    });

})

function fetchAptById(selectedAptId) {
    var serviceRegistryURL = "http://localhost:8080/disc"
    // get the apartment service url
    async function fetchServiceURL(serviceId) {
        try {
            let response = await $.ajax({
                url: serviceRegistryURL,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    code: ServiceMessageModel.SERVICE_DISCOVER_REQUEST,
                    data: serviceId
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
    async function fetchAptByIdData() {
        let requestURL = await fetchServiceURL(ServiceInfoModel.SERVICE_APT_LOAD);
        requestURL += ("/" + selectedAptId)
        console.log(requestURL);
        $.ajax({
            url: requestURL,
            type: 'GET',
            success: async function (response) {
                let apartment = JSON.parse(response)
                console.log(apartment)
                let poster = await fetchPosterInfo(apartment.posterID)
                console.log(poster)
                $('#aptNameContainer').text(apartment.aptName);
                $('#addressContainer').text(apartment.address);
                $('#priceContainer').text(apartment.price);
                $('#typeContainer').text(apartment.Type);
                $('#areaContainer').text(apartment.area);
                $('#dateContainer').text(apartment.availableDate);
                $('#posterNameContainer').text(poster.fullName);
                $('#posterEmailContainer').text(poster.email);
                $('#descrContainer').text(apartment.Descr);

            },
            error: function (error) {
                console.error('Error fetching apartment data:', error);
            }
        });
    }
    fetchAptByIdData()

    async function fetchPosterInfo(posterId) {
        let getUserInfoUrl = await fetchServiceURL(ServiceInfoModel.SERVICE_USER_LOAD);
        return new Promise((resolve, reject) => {
            $.ajax({
                url: getUserInfoUrl,
                type: 'POST',
                data: JSON.stringify(posterId),
                success: function (response) {
                    if (response.userID === -1){
                        alert("Username or Password is not correct");
                    } else {
                        console.log(response)
                        resolve(response);
                    }
                },
                error: function (error) {
                    console.error('Error fetching apartment data:', error);
                    reject(error);
                }
            });
        });
    }
}

function addApt2WishList(selectedAptId, userID) {
    var serviceRegistryURL = "http://localhost:8080/disc"
    // get the apartment service url
    async function fetchServiceURL() {
        try {
            let response = await $.ajax({
                url: serviceRegistryURL,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    code: ServiceMessageModel.SERVICE_DISCOVER_REQUEST,
                    data: ServiceInfoModel.SERVICE_WISHLIST_ADD
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
    async function addApt2WishListReq() {
        let requestURL = await fetchServiceURL();
        requestURL += ("/" + selectedAptId)
        console.log(requestURL);
        $.ajax({
            url: requestURL,
            type: 'POST',
            data: JSON.stringify({
                arg0: userID,
                arg1: selectedAptId
            }),
            success: function (response) {
                var responseData = JSON.stringify(response)
                if (responseData === -1) {
                    alert("Add to WishList Failed");
                } else {
                    alert("Add to WishList Success");
                }
            },
            error: function (error) {
                console.error('Error fetching apartment data:', error);
            }
        });
    }
    addApt2WishListReq()
}