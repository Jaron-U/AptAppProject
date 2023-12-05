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

    let selectedAptId = localStorage.getItem('selectedApartmentId');
    if (selectedAptId) {
        fetchAptById(selectedAptId)
    }
})

function fetchAptById(selectedAptId) {
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
                    data: ServiceInfoModel.SERVICE_APT_LOAD
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
        let requestURL = await fetchServiceURL();
        requestURL += ("/" + selectedAptId)
        console.log(requestURL);
        $.ajax({
            url: requestURL,
            type: 'GET',
            success: function (response) {
                let apartment = JSON.parse(response)
                console.log(apartment)
                $('#aptNameContainer').text(apartment.aptName);
                $('#addressContainer').text(apartment.address);
                $('#priceContainer').text(apartment.price);
                $('#typeContainer').text(apartment.Type);
                $('#areaContainer').text(apartment.area);
                $('#dateContainer').text(apartment.availableDate);
                $('#descrContainer').text(apartment.Descr);

            },
            error: function (error) {
                console.error('Error fetching apartment data:', error);
            }
        });
    }
    fetchAptByIdData()
}