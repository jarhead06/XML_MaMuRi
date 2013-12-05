function initialize() {
    var mapOptions = {
        center: new google.maps.LatLng(15, 15),
        zoom: 4
    };
    var map = new google.maps.Map(document.getElementById("map-canvas"),
            mapOptions);
}
google.maps.event.addDomListener(window, 'load', initialize);