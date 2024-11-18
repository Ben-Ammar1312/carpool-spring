var map;
var directionsService;
var directionsRenderers = {};

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: 34.0, lng: 9.0 }, // Adjust to your region
        zoom: 7
    });

    directionsService = new google.maps.DirectionsService();
}

function showRoute(button) {
    var annonceId = button.getAttribute('data-annonce-id');
    showRouteByAnnonceId(annonceId);
}

function showRouteByAnnonceId(annonceId) {
    var annonce = annoncesData.find(function(a) {
        return a.id_annonce == annonceId;
    });

    if (!annonce) {
        alert('Annonce data not found.');
        return;
    }

    // Remove existing routes
    for (var key in directionsRenderers) {
        directionsRenderers[key].setMap(null);
    }

    // Prepare waypoints
    var waypts = [];
    if (annonce.waypoints && annonce.waypoints.length > 0) {
        annonce.waypoints.forEach(function(waypoint) {
            waypts.push({
                location: new google.maps.LatLng(waypoint.latitude, waypoint.longitude),
                stopover: true
            });
        });
    }

    // Create a directions request
    var request = {
        origin: new google.maps.LatLng(annonce.departLat, annonce.departLng),
        destination: new google.maps.LatLng(annonce.arriveLat, annonce.arriveLng),
        waypoints: waypts,
        optimizeWaypoints: false,
        travelMode: google.maps.TravelMode.DRIVING
    };

    directionsService.route(request, function(response, status) {
        if (status === google.maps.DirectionsStatus.OK) {
            var directionsRenderer = new google.maps.DirectionsRenderer({
                map: map,
                directions: response,
                preserveViewport: false
            });
            directionsRenderers[annonceId] = directionsRenderer;
        } else {
            alert('Could not display directions due to: ' + status);
        }
    });
}