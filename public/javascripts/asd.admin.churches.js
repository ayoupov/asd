var churchEditClick = function () {
    var id = getId($(this));
    $churchWrapper.empty();

    $addressForm = newAddressForm();
    $churchWrapper.append($addressForm);
    $churchWrapper.append($("<br><br>"));

    $requestForm = newRequestForm();
    $churchWrapper.append($requestForm);
    $churchWrapper.append($("<br><br>"));

    $churchForm = newChurchForm();
    $("#submit", $churchForm).api({
        on: 'click',
        action: 'post update church',
        method: 'POST',
        onSuccess: apiResult,
        urlData: {id: id},
        serializeForm: true,
        beforeSend: function (settings) {
            $.extend(settings.data, $addressForm.serializeObject());
            return settings;
        }
    });
    $churchForm.appendTo($churchWrapper);
    $(".church-visible").show();
    if (id) {
        $churchForm.api({on: 'now', action: "get church passport", urlData: {id: id}, onSuccess: fillChurch});
        $requestForm.api({on: 'now', action: "get json requests", urlData: {id: id}, onSuccess: fillRequests});
    }
};

function newChurchForm() {
    return $("<div> Current church data: </div>" +
        "<form class='church-form ui form' method='post'>" +
        "<label for='name'>Name</label><input id='name' name='name'/>" +
        "<label for='synonyms'>synonyms</label><input type='text' id='synonyms' placeholder='synonyms' name='synonyms'/>" +
        "<label for='constructionStart'>construction start</label><input type='text' id='constructionStart' placeholder='Construction start' name='constructionStart'/>" +
        "<label for='constructionEnd'>construction end</label><input type='text' id='constructionEnd' placeholder='Construction end' name='constructionEnd'/>" +
        "<label for='architects'>architects</label><input type='text' id='architects' placeholder='Architects' name='architects'/>" +
        "<label for='website'>website</label><input type='text' id='website' placeholder='website' name='website'/>" +
        "<br>" +
        "<input id='submit' type='button' class='ui submit button' value='update'>" +
        "</form>");
}

function newRequestForm() {
    return $("<form class='request-form ui form' method='post'>" +
        "</form>");
}

function fillRequests(data) {
    if (data) {
        $requestForm.append("<div>Church has " + data.length + " requests</div>");
        $.each(data, function (a, item) {
            var $requestDiv = $("<div id='req-div_" + item.id + "' />");
            var by = (item.suggestedBy) ? item.suggestedBy.name : "unknown";
            var $entry = $("<label for='req_" + item.id + "'>" + "On " + item.field + " by " + by + "</label>" +
                "<input id='req_" + item.id + "' value='" + item[item.field] + "' >");
            $requestDiv.append($entry);
            var $fixButton = $("<label for='req-fix_" + item.id + "'>Mark as fixed</label>" +
                "<input type=checkbox id='req-fix_" + item.id + "' class='req-fix'>");
            var $ignoreButton = $("<label for='req-ignore_" + item.id + "'>Ignore</label>" +
                "<input type=checkbox id='req-ignore_" + item.id + "'  class='req-ignore'><br>");
            $requestDiv.append($fixButton);
            $requestDiv.append($ignoreButton);
            $requestForm.append($requestDiv);
        });
        $(".req-fix").api({
            on: 'click',
            action: 'post fix suggestion',
            method: 'POST',
            onSuccess: removeFromRequestForm,
            beforeSend: function (settings) {
                settings.urlData.id = getId($(this));
                return settings;
            }
        });
        $(".req-ignore").api({
            on: 'click',
            action: 'post ignore suggestion',
            method: 'POST',
            onSuccess: removeFromRequestForm,
            beforeSend: function (settings) {
                settings.urlData.id = getId($(this));
                return settings;
            }
        });
    }
}

function removeFromRequestForm(data) {
    $("#req-div_" + data.id, $requestForm).remove();
}

function fillChurch(data) {
    $admpages.hide();
    if (data) {
        //fillRequests(data.requests);
        $("#name", $churchForm).val(data.name);
        $("#constructionStart", $churchForm).val(data.constructionStart);
        $("#constructionEnd", $churchForm).val(data.constructionEnd);
        var architectsArr = [];
        if (data.architects)
            $.each(data.architects, function (a, item) {
                architectsArr.push(item.name);
            });
        $("#architects", $churchForm).val(architectsArr.join(","));
        $("#website", $churchForm).val(data.website);
        if (data.synonyms)
            $("#synonyms", $churchForm).val(data.synonyms.join(","));
        fillAddress(data);
        fillImages(data.images);
        fillLinks(data.media);
    }
    $churchWrapper.show();
}

function newAddressForm() {
    return $("<form class='address-form ui form' method='post'>" +
        "<div class='floating-panel'>" +
        "<input id='ca-address' type='textbox' value='Poland' name='address.unfolded'>" +
        "<input id='ca-lat' type='hidden' value='' name='address.lat'>" +
        "<input id='ca-lng' type='hidden' value='' name='address.lng'>" +
        "<input id='ca-geocode' type='button' value='Geocode'>" +
        "<input id='ca-revgeocode' type='button' value='Rev Geocode'>" +
        "</div>" +
        "<div class='church-address-map'></div>" +
        "<input id='ca-coords' type='textbox' value='' disabled='disabled'>" +
        "<input id='cagsv-id' type='hidden' value='' name='gsv.id'>" +
        "<input id='cagsv-link' type='textbox' value='' placeholder='Insert link of gsv here'>" +
        "<input id='cagsv-lat' type='textbox' value='' placeholder='lat' name='lat'>" +
        "<input id='cagsv-lng' type='textbox' value='' placeholder='lng' name='lng'>" +
        "<input id='cagsv-heading' type='textbox' value='' placeholder='heading' name='heading'>" +
        "<input id='cagsv-fov' type='textbox' value='' placeholder='fov' name='fov'>" +
        "<input id='cagsv-pitch' type='textbox' value='' placeholder='pitch' name='pitch'>" +
        "<label for='cagsv-usecustom'>Use this custom GSV</label>" +
        "<input id='cagsv-usecustom' type='checkbox' placeholder='pitch' name='use_custom'>" +
        "</form>");
}

var churchMap, geocoder, markers = [], churchLocation;
var $fp;

function fillAddress(data) {
    if (data.address) {
        $fp = $(".floating-panel");
        if (data.address.unfolded) {
            $("#ca-address", $fp).val(data.address.unfolded);
        }

        var opts = {zoom: 15, mapTypeId: google.maps.MapTypeId.HYBRID};
        if (data.address.geometry) {
            churchLocation = {lat: data.address.geometry[0], lng: data.address.geometry[1]};
            $.extend(opts, {
                center: churchLocation
            });
        }
        churchMap = new google.maps.Map($(".church-address-map")[0], opts);
        geocoder = new google.maps.Geocoder();
        //geocodeAddress();
        if (data.address.geometry) {
            placeMarker(churchLocation);
        }

        $('#ca-geocode').on('click', function () {
            geocodeAddress();
        });
        $('#ca-revgeocode').on('click', function () {
            reverseGeocode();
        });
        if (data.useCustomGSV)
            $('#cagsv-usecustom').prop('checked', true);

        if (data.gsv)
            fillServerGSV(data.gsv);

        $('#cagsv-link').on('keyup', fillGSV);

        google.maps.event.addListener(churchMap, 'click', function (event) {
            placeMarker(event.latLng);
        });

    }

}

function fillServerGSV(gsv) {
    $("#cagsv-lat").val(gsv.lat);
    $("#cagsv-lng").val(gsv.lng);
    $("#cagsv-fov").val(gsv.fov.toFixed(2));
    $("#cagsv-heading").val(gsv.heading.toFixed(2));
    $("#cagsv-pitch").val(gsv.pitch.toFixed(2));
    $("#cagsv-id").val(gsv.id);
}

function fillGSV() {
    // https://www.google.com/maps/@42.3455497,-71.0983238,3a,75y,336.06h,179t/data=!3m6!1e1!3m4!1sgAGtwPgJIB6zjGKBhGsXPg!2e0!7i13312!8i6656?hl=en-US
    var link = $('#cagsv-link').val();
    try {
        var data = /@(.*)\/data/.exec(link).pop();
        var split = data.split(',');
        $("#cagsv-lat").val(split[0]);
        $("#cagsv-lng").val(split[1]);
        $("#cagsv-fov").val(parseFloat(split[3]).toFixed(2));
        $("#cagsv-heading").val(parseFloat(split[4]).toFixed(2));
        $("#cagsv-pitch").val((parseFloat(split[5]) - 90.0).toFixed(2));
        //$("#cagsv-usecustom").prop('checked', true);
    } catch (e) {
        //console.log(e);
    }
}

function placeMarker(location) {
    removeMarkers();
    var marker = new google.maps.Marker({
        position: location,
        map: churchMap
    });
    pushMarker(marker);
    var lat = (typeof(location.lat) == "function") ? location.lat() : location.lat;
    var lng = (typeof(location.lng) == "function") ? location.lng() : location.lng;
    $("#ca-lat").val(lat);
    $("#ca-lng").val(lng);
    $("#ca-coords").val("Church coords: " + lat + " , " + lng);
}


function reverseGeocode() {
    var location = markers[0].position;
    geocoder.geocode({'location': location}, function (results, status) {
        if (status === google.maps.GeocoderStatus.OK) {
            //churchMap.setCenter(results[0].geometry.location);
            $("#ca-address", $fp).val(results[0].formatted_address);
        } else {
            alert('Geocode was not successful for the following reason: ' + status);
        }
    });

}

function geocodeAddress() {
    var address = document.getElementById('ca-address').value;
    geocoder.geocode({'address': address}, function (results, status) {
        if (status === google.maps.GeocoderStatus.OK) {
            churchMap.setCenter(results[0].geometry.location);
            removeMarkers();
            var marker = new google.maps.Marker({
                map: churchMap,
                position: results[0].geometry.location
            });
            pushMarker(marker);
        } else {
            alert('Geocode was not successful for the following reason: ' + status);
        }
    });
}

function removeMarkers() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

function pushMarker(marker) {
    markers.push(marker);
}

function fillImages(images) {
    if (images)
        $.each(images, function (a, item) {

        });
}

function fillLinks(media) {
    if (media && !$(".related-media", $churchWrapper).length) {
        $churchWrapper.append($("<div class='related-media'>Related media:</div>"));
        $.each(media, function (a, item) {
            var $entry = $("<a href='/" + item.contentType.toLowerCase() + "/" + item.id + "' >" + item.title + "</a><br>");
            $entry.appendTo($churchWrapper);
        });
    }

}

var $newChurchAdminForm;

function updateChurchAdminFormExtId(data) {
    $("#nc-ext-id", $newChurchAdminForm).val(data.ext_id);
}

function addChurchAdmin(name, address, userId, suggestionId) {
    $newChurchAdminForm = $(".new-church-admin-form");
    $("#nc-name", $newChurchAdminForm).val(name);
    $("#nc-address", $newChurchAdminForm).val(address);
    $("#nc-user-id", $newChurchAdminForm).val(userId);
    $("#nc-suggestion-id", $newChurchAdminForm).val(suggestionId);

    $(".ui.selection", $newChurchAdminForm).dropdown(
        {
            onChange: function (value, text, $choice) {
                $(this).api({
                    on: 'now',
                    action: 'get ext id',
                    method: 'GET',
                    urlData: {
                        id: value
                    },
                    onSuccess: updateChurchAdminFormExtId
                });
            }
        }
    );

    $(".ui.submit", $newChurchAdminForm).api({
        on: 'click',
        serializeForm: true,
        action: 'add new church',
        method: 'POST',
        onSuccess: reloadPage,
        beforeSend: function(settings){
            settings.urlData.id = $("#nc-ext-id", $newChurchAdminForm).val();
            return settings;
        }
    });

    $newChurchAdminForm.modal('show');
}