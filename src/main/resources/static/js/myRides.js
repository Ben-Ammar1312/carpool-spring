var map;
var directionsService;
var directionsRenderer;
var currentAnnonceId = null;
var currentRoutePolyline = null; // Polyline representing the current route
var bookingMode = false;
var mapClickListener; // Variable to hold the map click listener

let pickupMarkers = []; // Array to keep track of pickup point markers

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        // Adjust initial map options as needed
        center: { lat: 36.8065, lng: 10.1815 },
        zoom: 12, // Set an appropriate zoom level
    });
    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
        suppressPolylines: true,
        suppressMarkers: true
    });
}

window.onload = initMap;

// Function to clear existing pickup markers
function clearPickupMarkers() {
    pickupMarkers.forEach(marker => marker.setMap(null));
    pickupMarkers = [];
}

function showRoute(button) {
    var annonceId = button.getAttribute("data-annonce-id");
    currentAnnonceId = annonceId;
    var annonce = annoncesData.find(a => a.id_annonce == annonceId);

    if (!annonce) {
        console.error("Annonce not found for ID:", annonceId);
        return;
    }

    // Clear previous pickup markers
    clearPickupMarkers();

    var waypoints = [];
    // If you need waypoints for routing, but not to display markers
    if (annonce.waypoints && annonce.waypoints.length > 0) {
        waypoints = annonce.waypoints.map(wp => {
            return {
                location: new google.maps.LatLng(wp.latitude, wp.longitude),
                stopover: true
            };
        });
    }

    var request = {
        origin: new google.maps.LatLng(annonce.departLat, annonce.departLng),
        destination: new google.maps.LatLng(annonce.arriveLat, annonce.arriveLng),
        waypoints: waypoints,
        travelMode: google.maps.TravelMode.DRIVING,
    };

    directionsService.route(request, function (result, status) {
        if (status === google.maps.DirectionsStatus.OK) {
            directionsRenderer.setDirections(result);
            // Remove previous polyline if any
            if (currentRoutePolyline) {
                currentRoutePolyline.setMap(null);
            }
            var routePath = [];
            var legs = result.routes[0].legs;
            for (var i = 0; i < legs.length; i++) {
                var steps = legs[i].steps;
                for (var j = 0; j < steps.length; j++) {
                    var nextSegment = steps[j].path;
                    for (var k = 0; k < nextSegment.length; k++) {
                        routePath.push(nextSegment[k]);
                    }
                }
            }

            // Only add pickup point markers
            if (annonce.pickupPoints && annonce.pickupPoints.length > 0) {
                annonce.pickupPoints.forEach(pickup => {
                    if (pickup.latitude && pickup.longitude) {
                        let marker = new google.maps.Marker({
                            position: { lat: pickup.latitude, lng: pickup.longitude },
                            map: map,
                            icon: {
                                url: '/images/flag_icon.png', // Ensure this path is correct
                                scaledSize: new google.maps.Size(32, 32)
                            },
                            title: 'Pickup Point'
                        });
                        pickupMarkers.push(marker);
                    }
                });
            } else {
                console.log("No pickup points for annonce ID:", annonce.id_annonce);
            }

            currentRoutePolyline = new google.maps.Polyline({
                path: routePath,
                strokeColor: "#FF0000",
                strokeOpacity: 1.0,
                strokeWeight: 2,
                clickable: false // Ensure clicks pass through to the map
            });
            currentRoutePolyline.setMap(map);

            // Adjust map bounds to fit the route
            var bounds = new google.maps.LatLngBounds();
            for (var i = 0; i < routePath.length; i++) {
                bounds.extend(routePath[i]);
            }
            map.fitBounds(bounds);
        } else {
            alert("Could not display directions due to: " + status);
        }
    });
}

function bookRide(button) {
    var annonceId = button.getAttribute("data-annonce-id");
    currentAnnonceId = annonceId;

    // Ensure the route is displayed
    showRoute(button);

    // Enter booking mode
    bookingMode = true;
    // Show the booking banner
    document.getElementById('booking-banner').style.display = 'block';

    // Change the cursor to indicate selection mode
    map.setOptions({ draggableCursor: 'crosshair' });

    // Show instructions to the user
    alert("Please click on the map to select your pickup point.");

    // Remove previous listener if exists
    if (mapClickListener) {
        google.maps.event.removeListener(mapClickListener);
    }

    // Add click listener to the map
    mapClickListener = map.addListener('click', function(event) {
        if (!bookingMode) return;

        var pickupLatLng = event.latLng;

        // Check if the point is on the route
        var isOnRoute = google.maps.geometry.poly.isLocationOnEdge(pickupLatLng, currentRoutePolyline, 1e-4); // Tolerance in degrees (~11 meters)

        if (isOnRoute) {
            alert("Pickup point is on the route. Proceeding with booking.");
            sendBookingRequest(pickupLatLng, currentAnnonceId, true);
        } else {
            // Calculate tolerance for 2km
            var toleranceInMeters = 2000; // 2km
            var metersPerDegree = 111320; // Approximate value for latitude degrees
            var toleranceInDegrees = toleranceInMeters / metersPerDegree; // ≈ 0.01796 degrees

            // Check if the point is within 2km of the route
            var isNearRoute = google.maps.geometry.poly.isLocationOnEdge(pickupLatLng, currentRoutePolyline, toleranceInDegrees);

            if (isNearRoute) {
                alert("Pickup point is within 2km of the route. Sending request to the driver for approval.");
                sendBookingRequest(pickupLatLng, currentAnnonceId, false);
            } else {
                alert("Pickup point is too far from the route. Please select a point closer to the route.");
            }
        }

        // Exit booking mode and remove listener
        bookingMode = false;
        // Hide the booking banner
        document.getElementById('booking-banner').style.display = 'none';

        // Reset the cursor to default
        map.setOptions({ draggableCursor: null });
        google.maps.event.removeListener(mapClickListener);
    });
}

function sendBookingRequest(pickupLatLng, annonceId, isOnRoute) {
    // Prepare data
    var data = {
        annonceId: annonceId,
        pickupLat: pickupLatLng.lat(),
        pickupLng: pickupLatLng.lng(),
        onRoute: isOnRoute
    };

    // Send AJAX request to server
    fetch('/bookRide', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
            // Include CSRF token if necessary
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(result => {
            alert(result.message);
            // Check if onRoute was false, meaning we are waiting for driver approval
            if (!data.onRoute) {
                // Find the "Book" button for this annonce and change its text to "En Attente"
                var btn = document.querySelector('button[data-annonce-id="' + data.annonceId + '"][onclick="bookRide(this)"]');
                if (btn) {
                    btn.innerText = "En Attente";
                    btn.classList.remove('btn-success');
                    btn.classList.add('btn-warning');
                    btn.disabled = true;
                }
            } else {
                // If it's on-route booking and got confirmed immediately, just reload to show the new status
                location.reload();
            }
        })
        .catch(error => {
            alert('Error booking ride');
            console.error(error);
        });
}

function cancelBooking(button) {
    var annonceId = button.getAttribute("data-annonce-id");

    fetch('/cancelBooking', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
            // Include CSRF token if necessary
        },
        body: JSON.stringify({ annonceId: annonceId })
    })
        .then(response => response.json())
        .then(result => {
            alert(result.message);
            // Refresh the page to update button states
            location.reload();
        })
        .catch(error => {
            alert('Error cancelling booking');
            console.error(error);
        });
}