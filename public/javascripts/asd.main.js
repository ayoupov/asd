var localdebug = true, apidebug = true, usemap = false;

var local_paths = {
    metropolies: '/assets/metropolies_wgs84.topojson',
    diecezje: '/assets/diecezje_wgs84.topojson'
};

var prod_paths = {
    metropolies: '/asd/metropolies_wgs84.topojson',
    diecezje: '/asd/diecezje_wgs84.topojson'
};

var metropStyle = function (feature) {
    return {
        color: '#d3d3d3',
        fillColor: 'transparent',
        weight: 3
    };
};

var dieStyle = function (feature) {
    return {
        color: '#bdbdbd',
        fillColor: 'transparent',
        weight: 2
    };
};

$(document).ready(function () {
    stickify();
    isotopeThumbs();
    uiInit();
    resizeFunc();
    if (usemap)
        mapInit();
});

var map;

var mapInit = function () {
    L.mapbox.accessToken = 'pk.eyJ1IjoiYXlvdXBvdiIsImEiOiJjYTc1MDkyY2ZlZDIyOGE3Mjc2NzE1ODk3Yzg0OGRlMSJ9.TLk_UalfiCktwxGqd9kRmg';
    var opts = {};
    map = L.mapbox.map('map', 'ayoupov.09a5336b', opts).setView([52.36, 18.45], 6);
    $('.leaflet-control-attribution').css(
        {"display": 'inline', 'clear': 'none'}
    ).appendTo($('.leaflet-bottom.leaflet-left'));
    $('.mapbox-logo').css('padding-right', '10px');
    map.options.doubleClickZoom = true;
    map.options.minZoom = 6;
    map.scrollWheelZoom.disable();

    var customMetropLayer = L.geoJson(null, {style: metropStyle});
    var customDieLayer = L.geoJson(null, {style: dieStyle});
    var metropoliesLayer = omnivore.topojson(localdebug ? local_paths.metropolies : prod_paths.metropolies, null, customMetropLayer);
    metropoliesLayer.addTo(map);

    var diecezjeLayer = omnivore.topojson(localdebug ? local_paths.diecezje : prod_paths.diecezje, null, customDieLayer);
    diecezjeLayer.addTo(map);

    map.on('zoomend ', function (e) {
        if (map.getZoom() < 8) {
            map.removeLayer(diecezjeLayer)
        }
        else if (map.getZoom() >= 8) {
            map.addLayer(diecezjeLayer)
        }
    });

    map.fire('zoomend');
};

