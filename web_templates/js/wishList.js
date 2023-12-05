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

        // fetchWishListApts();
        fetchWishListId(userObject.userID)

    } else {
        $('#loginLink').show();
        $('#logoutLink').hide();
        $('#userDisplay').hide();
    }

    $('.listing-container').on('click', '.listing', function() {
        const apartmentId = $(this).data('id');
        console.log(apartmentId);
        localStorage.setItem('selectedApartmentId', apartmentId);
        location.href = 'aptDetail.html';
    });
});

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

async function fetchWishListId(userId) {
    let requestURL = await fetchServiceURL(ServiceInfoModel.SERVICE_WISHLIST_LOAD);
    let requestAptByIdUrl = await fetchServiceURL(ServiceInfoModel.SERVICE_APT_LOAD);
    console.log(requestURL);
    console.log(userId)
    $.ajax({
        url: requestURL,
        type: 'POST',
        data: JSON.stringify(userId),
        success: async function (response) {
            let apartmentHtmlList = await Promise.all(response.map(aptId =>
                fetchAptByIdData(requestAptByIdUrl, aptId)
            ));

            let listingsHtml = '';
            apartmentHtmlList.forEach(html => {
                listingsHtml += html;
            });

            $('.listing-container').html(listingsHtml);
        },
        error: function (error) {
            console.error('Error fetching apartment data:', error);
        }
    });
}

async function fetchAptByIdData(requestURL, aptId) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `${requestURL}/${aptId}`,
            type: 'GET',
            success: function (response) {
                let apartment = JSON.parse(response);
                let apartmentHtml = `
                    <div class="listing" data-id='${aptId}'>
                        <span class="apartment-name">Apartment Name: ${apartment.aptName}</span> -- 
                        <span class="apartment-price">Price: $${apartment.price}</span> -- 
                        <span class="apartment-type">Type: ${apartment.Type}</span> -- 
                        <span class="apartment-address">Address: ${apartment.address}</span>
                    </div>
                `;
                resolve(apartmentHtml);
            },
            error: function (error) {
                console.error('Error fetching apartment data:', error);
                reject(error);
            }
        });
    });
}