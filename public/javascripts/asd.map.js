var dekanatsEnabled = false;

var feature_paths = {
    metropolies: '/assets/metropolies_wgs84_10percent.topojson',
    diecezje: '/assets/diecezje_wgs84_10percent.topojson'
    //dekanaty: '/assets/dekanaty_wgs84_10percent.topojson'
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

var uniStyle = function (feature) {
    return {
        color: '#4d4d4d',
        weight: 2,
        dashArray: '1,5',
        fillColor: 'transparent'
    };
};

//var dekStyle = function (feature) {
//    return {
//        color: 'white',
//        //color: 'green',
//        fillColor: 'transparent',
//        weight: 1
//    };
//};

$(document).ready(function () {
    checkReturn();
    stickify();
    isotopeThumbs();
    uiInit();
    resizeFunc();
});

var map, customMetropLayer,
    metropoliesLayer, dekanatyLayer, diecezjeLayer, churchesLayer,
    metroCountersLayer, dioCounterLayer,
    comingBack = false;

function checkReturn() {
    var cb = Cookies.get('auth.cb');
    if (cb && !userAuthed) {
        Cookies.remove('auth.cb');
        Cookies.set('al.cb', true);
        window.location = cb;
    }
}

var mapInit = function (geostats) {
    L.mapbox.accessToken = mapboxAccessToken;
    var opts = {
        loadingControl: true
    };
    map = L.mapbox.map('map', mapboxMapID, opts).setView([52.36, 18.45], 7);
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
    map.options.maxZoom = 13;

    addChurchContents();
    if (dekanatsEnabled)
        addDekanatsContent();

    customMetropLayer = L.geoJson(null, {
        //style: metropStyle
        style: uniStyle

        //, pointToLayer: function (feature, latlng) {
        //    return L.marker(latlng,
        //        {
        //            icon: L.divIcon({html: feature.properties.count})
        //        });
        //}
    });

    var customDieLayer = L.geoJson(null, {
        //style: dieStyle
        style: uniStyle
    });
    //var customDekLayer = L.geoJson(null, {style: dekStyle});

    metropoliesLayer = omnivore.topojson(feature_paths.metropolies, null, customMetropLayer);
    metropoliesLayer.on('dblclick', function (ev) {
        map.fire('dblclick', ev);
    });

    diecezjeLayer = omnivore.topojson(feature_paths.diecezje, null, customDieLayer);
    diecezjeLayer.on('dblclick', function (ev) {
        map.fire('dblclick', ev);
    });

    metroCountersLayer = L.geoJson();
    dioCounterLayer = L.geoJson();
    addLayerCounters(metroCountersLayer, geostats.metro);
    addLayerCounters(dioCounterLayer, geostats.dio);

    map.on('zoomanim', layerChanges);

    layerChanges();

    comingBack = (userAuthed && Cookies.get('auth.cb')) || (!userAuthed && Cookies.get('al.cb'));
    mapPostLoad(comingBack);

    map.on('popupopen', function (popup) {
        $("a.open-passport", $(popup.target._container)).off('click').on('click', function () {
            openPassport(getId($(this)))
        });
    });

    // map wheel zoom
    setSiteMode();
    map.on('click', function () {
        setMapMode();
    });
    map.on('move', function () {
        setMapMode();
    });
    metropoliesLayer.on('click', function () {
        setMapMode();
    });
    metropoliesLayer.on('move', function () {
        setMapMode();
    });
    diecezjeLayer.on('click', function () {
        setMapMode();
    });
    diecezjeLayer.on('move', function () {
        setMapMode();
    });
    $('body,#links > a').click(function (evt) {
        if (evt.target.id == "map")
            return;
        if ($(evt.target).closest('#map').length)
            return;
        setSiteMode();
    });

};

function setMapMode() {
    if (map)
        map.scrollWheelZoom.enable();
}

function setSiteMode() {
    if (map)
        map.scrollWheelZoom.disable();
}

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
        if (dekanatsEnabled) map.removeLayer(dekanatyLayer);
        if (zoom >= 8) {
            if (!map.hasLayer(dioCounterLayer)) map.addLayer(dioCounterLayer);
            if (!map.hasLayer(diecezjeLayer)) map.addLayer(diecezjeLayer);
        }
    }
    else {
        map.removeLayer(dioCounterLayer);
        map.removeLayer(diecezjeLayer);
        if (dekanatsEnabled) if (!map.hasLayer(dekanatyLayer)) map.addLayer(dekanatyLayer);
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

var defaultOffset = new L.point(0, -32);

function addLayerCounters(layer, data) {
    $(data).each(function (a, item) {
        var id = item[0];
        var count = item[1];
        var ll = item[2];
        var name = item[3];
        var marker = L.marker(ll, {
            icon: L.divIcon({
                html: count,
                iconSize: [78, 78],
                className: 'church-counter-divicon'
            })
        });
        var popupString = "<div class='count-markers-popup'>" + name + "</div>";
        var popupOpts = {offset: defaultOffset};
        if (possiblyDesktop) {
            marker.bindPopup(popupString, $.extend(popupOpts, {closeButton: false}));
            marker.on('mouseover', function (e) {
                this.openPopup();
            });
            marker.on('mouseout', function (e) {
                this.closePopup();
            });
            marker.on('click', function () {
                map.setView(marker.getLatLng(), layer == metroCountersLayer ? 8 : 11);
                layerChanges();
            });
        } else {
            marker.bindPopup(popupString, popupOpts);

            marker.on('click', function () {
                map.setView(marker.getLatLng(), layer == metroCountersLayer ? 8 : 11);
                layerChanges();
            });
        }
        marker.addTo(layer);
    });
}

function addDekanatsContent() {

    dekanatyLayer = new L.TileLayer.GeoJSON(dekanatTilesURL, {
            //clipTiles: true,
            unique: function (feature) {
                return feature.properties.id;
            },
            minZoom: 10,
            maxZoom: 18,
            subdomains: 'abc'
        }, {
            style: dekStyle,
            onEachFeature: function (feature, layer) {
                layer.on({
                    'dblclick': function (ev) {
                        map.fire('dblclick', ev);
                    }
                });
            }
        }
    );

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

    churchesLayer = new L.TileLayer.GeoJSON(churchTilesURL, {
            //clipTiles: true,
            unique: function (feature) {
                return feature.properties.id;
            },
            minZoom: 10,
            maxZoom: 18,
            subdomains: 'abc'
        }, {
            style: style,
            pointToLayer: function (feature, latlng) {
                return L.marker(latlng, churchMarkerStyle);
            },
            onEachFeature: function (feature, layer) {
                if (feature.properties) {
                    //var popupString = getPopup(feature, layer);
                    var popupOpts = {};
                    var popupString;
                    if (possiblyDesktop) {
                        popupString = "<div class='church-popup'>" + feature.properties.name + "</div>";
                        $.extend(popupOpts, {closeButton: false});
                        layer.bindPopup(popupString, popupOpts);
                        layer.on({
                            click: function (e) {
                                whenChurchChosen(e);
                                openPassport(e.target.feature.properties.ext_id, function () {
                                    map.setView(currentChurch.address.geometry, 14);
                                    layerChanges();
                                });
                            },
                            mouseover: function (e) {
                                e.layer.openPopup();
                                //whenChurchChosen(e);
                            },
                            mouseout: function (e) {
                                e.layer.closePopup();
                                //replaceHistoryWithChurch();
                            }
                        });
                    } else {
                        popupString = getPopup(feature, layer);
                        layer.bindPopup(popupString, popupOpts);
                        layer.on({
                            click: whenChurchChosen
                        });
                    }
                }
            }
        }
    );
}

var POPUP_ON_LOAD_ONCE = true;

function navigateFromSearch(id) {
    updateHistoryWithChurch(id, "#passport");
    openPassport(id, function () {
        map.setView(currentChurch.address.geometry, 14);
        layerChanges();
    });
}

function navigateTo(church, comingBack) {
    churchesLayer.geojsonLayer.on('layeradd', function (evt) {
        var marker = evt.layer;
        if (marker.feature.properties.ext_id == church.extID) {
            if (POPUP_ON_LOAD_ONCE) {
                churchesLayer.geojsonLayer.off('layeradd');
                marker.openPopup();
                if (stringStartsWith(location.hash, "#passport"))
                    openPassport(church.extID, comingBack || location.hash == "#passportadd" ? function () {
                        // todo: huh? wait for gsv to load?
                        $passportWrapper.on('passportready', function () {
                            showPassportUpdateForm();
                            $passportWrapper.off('passportready');
                            //$passportWrapper.parent().scrollTop($passportUpdate.offset().top);
                        });
                    } : null);
            }
        }
    });
    map.setView(church.geometry, 14);
    layerChanges();
}

function mapPostLoad(comingBack) {
    if (typeof currentChurch !== "undefined") {
        if (comingBack) {
            var port = location.port;
            replaceHistoryWithChurch(currentChurch.extID, "#passport");
            Cookies.remove('auth.cb');
            Cookies.remove('al.cb');
        }
        navigateTo(currentChurch, comingBack);
    }
}

function getPopup(feature, layer) {
    var popupString = '<div class="church-popup">';
    popupString += feature.properties.name;
    popupString += '<br/>';
    popupString += '<a href="#passport" class="open-passport" id="passport_' + feature.properties.ext_id + '">Details</a>';
    popupString += '</div>';
    return popupString;
}

function openPassport(id, callback) {
    $passportWrapper.api({
        on: 'now',
        action: 'get church passport',
        method: 'GET',
        urlData: {id: id},
        onSuccess: function (data) {
            fillPassport(data);
            replaceHistoryWithChurch(currentChurch.extID, "#passport");
            $passportWrapper.modal('show');
            if (callback)
                callback();
        }
    });
}

function whenChurchChosen(e) {
    var id = e.target.feature.properties.ext_id;
    updateHistoryWithChurch(id);
}

function replaceHistoryWithChurch(id, hash) {
    var uri = '';
    if (id)
        uri = "/church/" + id;
    window.history.replaceState({}, "",
        "http://" + location.hostname + (port != "" ? ":" + port : "") + uri +
        ((hash) ? hash : ""));
}

function updateHistoryWithChurch(id, hash) {
    window.history.pushState({}, "",
        "http://" + location.hostname + (port != "" ? ":" + port : "") + "/church/" + id +
        ((hash) ? hash : ""));
}