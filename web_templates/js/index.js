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
        $('#logoutLink').click(function() {
            localStorage.clear(); // clean local storage
            location.reload();
        });
    } else {
        $('#loginLink').show();
        $('#logoutLink').hide();
        $('#userDisplay').hide();
    }

    fetchAllApartments();
});

function fetchAllApartments() {
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
                    data: ServiceInfoModel.SERVICE_APT_LOADALL
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
    async function fetchAllApts() {
        let requestURL = await fetchServiceURL();
        console.log(requestURL);
        $.ajax({
            url: requestURL,
            type: 'GET',
            success: function (response) {
                let apartments = JSON.parse(response)
                console.log(apartments)
                let listingsHtml = '';
                apartments.forEach(apartment => {
                    listingsHtml += `
                        <div class="listing">
                            <span class="apartment-name">${apartment.aptName}</span> - 
                            <span class="apartment-price">${apartment.price}</span> - 
                            <span class="apartment-type">${apartment.Type}</span> - 
                            <span class="apartment-address">${apartment.address}</span>
                        </div>
                    `;
                });
                $('.listing-container').html(listingsHtml);
            },
            error: function (error) {
                console.error('Error fetching apartment data:', error);
            }
        });
    }
    fetchAllApts()
}


