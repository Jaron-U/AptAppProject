import ServiceMessageModel from '/js/ServiceMessageModel.js';
import ServiceInfoModel from "/js/ServiceInfoModel.js";

$(document).ready(function() {
    let storedUserData = localStorage.getItem('user');
    localStorage.removeItem('selectedApartmentId');
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

    $('#wishListLink').click(function (event) {
        if (!storedUserData) {
            alert("Please Login First")
            event.preventDefault()
        }
    })

    fetchAllApartments();

    $('#search-button').click(function() {
        let minPrice = $('#lowPrice').val();
        let maxPrice = $('#highPrice').val();
        let type = $('#type').val();
        if (minPrice !== "" && maxPrice !== "") {
            // get apts list within this price range
            fetchAptsByPrice(minPrice, maxPrice);
        } else if (type != null) {
            // get apts list by type
            fetchAptsByType(type);
        }
    });

    $('.listing-container').on('click', '.listing', function() {
        const apartmentId = $(this).data('id');
        console.log(apartmentId);
        localStorage.setItem('selectedApartmentId', apartmentId);
        location.href = 'aptDetail.html';
    });
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
                        <div class="listing" data-id='${apartment.id}'>
                            <span class="apartment-name">Apartment Name: ${apartment.aptName}</span> -- 
                            <span class="apartment-price">Price: $${apartment.price}</span> -- 
                            <span class="apartment-type">Type: ${apartment.Type}</span> -- 
                            <span class="apartment-address">Address: ${apartment.address}</span>
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

function fetchAptsByPrice(minPrice, maxPrice) {
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
                    data: ServiceInfoModel.SERVICE_APT_SEARCH_PRICE
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
    async function fetchAptsByPriceData() {
        let requestURL = await fetchServiceURL();
        console.log(requestURL);
        $.ajax({
            url: requestURL,
            type: 'POST',
            data: JSON.stringify({
                lowPrice: minPrice,
                highPrice: maxPrice
            }),
            success: function (response) {
                let apartments = JSON.parse(response)
                console.log(apartments)
                let listingsHtml = '';
                apartments.forEach(apartment => {
                    listingsHtml += `
                        <div class="listing" data-id='${apartment.id}'>
                            <span class="apartment-name">Apartment Name: ${apartment.aptName}</span> -- 
                            <span class="apartment-price">Price: $${apartment.price}</span> -- 
                            <span class="apartment-type">Type: ${apartment.Type}</span> -- 
                            <span class="apartment-address">Address: ${apartment.address}</span>
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
    fetchAptsByPriceData()
}

function fetchAptsByType(type) {
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
                    data: ServiceInfoModel.SERVICE_APT_SEARCH_TYPE
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
    async function fetchAptsByTypeData() {
        let requestURL = await fetchServiceURL();
        requestURL += ("/" + type)
        console.log(requestURL)
        $.ajax({
            url: requestURL,
            type: 'GET',
            success: function (response) {
                let apartments = JSON.parse(response)
                console.log(apartments)
                let listingsHtml = '';
                apartments.forEach(apartment => {
                    listingsHtml += `
                        <div class="listing" data-id='${apartment.id}'>
                            <span class="apartment-name">Apartment Name: ${apartment.aptName}</span> -- 
                            <span class="apartment-price">Price: $${apartment.price}</span> -- 
                            <span class="apartment-type">Type: ${apartment.Type}</span> -- 
                            <span class="apartment-address">Address: ${apartment.address}</span>
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
    fetchAptsByTypeData()
}

