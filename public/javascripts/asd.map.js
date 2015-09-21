var feature_paths = {
    metropolies: '/assets/metropolies_wgs84_10percent.topojson',
    diecezje: '/assets/diecezje_wgs84_10percent.topojson',
    dekanaty: '/assets/dekanaty_wgs84_10percent.topojson'
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

var dekStyle = function (feature) {
    return {
        color: 'white',
        fillColor: 'transparent',
        weight: 1
    };
};

$(document).ready(function () {
    stickify();
    isotopeThumbs();
    uiInit();
    resizeFunc();
    //if (usemap)
    //    mapInit();
});

var map, customMetropLayer,
    metropoliesLayer, dekanatyLayer, diecezjeLayer, churchesLayer,
    metroCountersLayer, dioCounterLayer;

var mapInit = function (geostats) {
    L.mapbox.accessToken = 'pk.eyJ1IjoiYXlvdXBvdiIsImEiOiJjYTc1MDkyY2ZlZDIyOGE3Mjc2NzE1ODk3Yzg0OGRlMSJ9.TLk_UalfiCktwxGqd9kRmg';
    var opts = {
        loadingControl: true
    };
    map = L.mapbox.map('map', 'ayoupov.09a5336b', opts).setView([52.36, 18.45], 7);
    // relocate attribution
    $('.leaflet-control-attribution').css(
        {
            "display": 'inline',
            'clear': 'none'
        }
    )
        .appendTo($('.leaflet-bottom.leaflet-left'));
    $('.mapbox-logo').css('padding-right', '10px');

    map.options.doubleClickZoom = true;
    map.options.minZoom = 7;
    map.options.maxZoom = 18;
    map.scrollWheelZoom.disable();

    addChurchContents();

    customMetropLayer = L.geoJson(null, {
        style: metropStyle
        //, pointToLayer: function (feature, latlng) {
        //    return L.marker(latlng,
        //        {
        //            icon: L.divIcon({html: feature.properties.count})
        //        });
        //}
    });

    var customDieLayer = L.geoJson(null, {style: dieStyle});
    var customDekLayer = L.geoJson(null, {style: dekStyle});

    metropoliesLayer = omnivore.topojson(feature_paths.metropolies, null, customMetropLayer);
    metropoliesLayer.on('dblclick', function (ev) {
        map.fire('dblclick', ev);
    });

    diecezjeLayer = omnivore.topojson(feature_paths.diecezje, null, customDieLayer);
    diecezjeLayer.on('dblclick', function (ev) {
        map.fire('dblclick', ev);
    });

    dekanatyLayer = omnivore.topojson(feature_paths.dekanaty, null, customDekLayer);
    dekanatyLayer.on('dblclick', function (ev) {
        map.fire('dblclick', ev);
    });

    metroCountersLayer = L.geoJson();
    dioCounterLayer = L.geoJson();
    addLayerCounters(metroCountersLayer, geostats.metro);
    addLayerCounters(dioCounterLayer, geostats.dio);

    map.on('zoomanim', layerChanges);

    //map.fire('zoomanim');
    layerChanges();
    mapPostLoad();
};

var layerChanges = function (e) {
    var zoom;
    if (!e || !e.zoom) zoom = map.getZoom();
    else zoom = e.zoom;

    if (zoom < 8) {
        map.removeLayer(dioCounterLayer);
        map.removeLayer(diecezjeLayer);
        if (!map.hasLayer(metropoliesLayer)) map.addLayer(metropoliesLayer);
        if (!map.hasLayer(metroCountersLayer)) map.addLayer(metroCountersLayer);
    }
    else {
        map.removeLayer(metroCountersLayer);
        map.removeLayer(metropoliesLayer);
    }

    if (zoom < 11) {
        map.removeLayer(churchesLayer);
        map.removeLayer(dekanatyLayer);
        if (zoom >= 8) {
            if (!map.hasLayer(dioCounterLayer)) map.addLayer(dioCounterLayer);
            if (!map.hasLayer(diecezjeLayer)) map.addLayer(diecezjeLayer);
        }
    }
    else {
        map.removeLayer(dioCounterLayer);
        map.removeLayer(diecezjeLayer);
        if (!map.hasLayer(dekanatyLayer)) map.addLayer(dekanatyLayer);
        if (!map.hasLayer(churchesLayer)) map.addLayer(churchesLayer);
    }
};

var churchIcon = L.icon(
    {
        iconUrl: '/assets/images/church_marker.png',
        iconSize: [43, 59],
        iconAnchor: [22, 59],
        popupAnchor: [0, -59]
    });

function addLayerCounters(layer, data) {
    $(data).each(function (a, item) {
        var id = item[0];
        var count = item[1];
        var ll = item[2];
        L.marker(ll, {
            icon: L.divIcon({
                html: count,
                iconSize: [78, 78],
                className: 'church-counter-divicon'
            })
        }).addTo(layer);
    });
}

function addChurchContents() {

    var churchMarkerStyle = {
        icon: churchIcon
    };
    var style = {
        "clickable": true,
        "color": "#00D",
        "fillColor": "#00D",
        "weight": 1.0,
        "opacity": 0.3,
        "fillOpacity": 0.2
    };
    var hoverStyle = {
        "fillOpacity": 0.5
    };

    var port = location.port;
    var geojsonURL = 'http://' + location.hostname + (port != "" ? ":" + port : "") + '/tiles/c/{z}/{x}/{y}.json';
    churchesLayer = new L.TileLayer.GeoJSON(geojsonURL, {
            //clipTiles: true,
            unique: function (feature) {
                return feature.properties.id;
            },
            minZoom: 10,
            maxZoom: 18
        }, {
            style: style,
            pointToLayer: function (feature, latlng) {
                return L.marker(latlng, churchMarkerStyle);
            },
            onEachFeature: function (feature, layer) {
                if (feature.properties) {
                    var popupString = '<div class="popup">';
                    for (var k in feature.properties) {
                        var v = feature.properties[k];
                        popupString += k + ': ' + v + '<br />';
                    }
                    popupString += '</div>';
                    layer.bindPopup(popupString);
                }
            }
        }
    );
}

var POPUP_ON_LOAD_ONCE = true;

function navigateTo(church) {
    //churchesLayer.on('ready', function () {
    churchesLayer.geojsonLayer.on('layeradd', function (evt) {
        var marker = evt.layer;
        if (marker.feature.properties.ext_id == church.extID) {
            marker.openPopup();
            if (POPUP_ON_LOAD_ONCE)
                churchesLayer.geojsonLayer.off('layeradd');
        }
    });
    // todo: produces exception : Uncaught TypeError: Cannot read property 'min' of undefined
    // seems ^^^ from geojsonLayer
    map.setView(church.address.geometry, 14);
    layerChanges();
}

function mapPostLoad() {
    if (typeof currentChurch !== "undefined")
        navigateTo(currentChurch);
}