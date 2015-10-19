var $churchStoriesTitle, $passportUpdateButton, $passportGalleryThumbs,
    $passportUpdate, $newStoryGallery, $passportGallery, $passportWrapper;

var $passportCurrentImage;

$(document).ready(function () {
    $passportWrapper = $(".passport-wrapper");
    $passportGallery = $(".passport-gallery");

    $churchStoriesTitle = $(".passport-stories-title");

    $passportUpdate = $(".passport-update");
    $newStoryGallery = $(".story-cover-gallery");
    $passportUpdateButtonWrapper = $(".passport-update-button-wrapper");

    $(".close-button").on('click', function () {
        $passportWrapper.modal('hide');
    });
    $passportWrapper.modal({
        onHidden: function () {
            removeHash();
        }
    });

    // messages
    $('.ui.message', $passportWrapper)
        .off('click')
        .on('click', function () {
            $(this).fadeOut('slow');
        })
        .hide();

    initUpdatePassportApi();
    initFieldUpdates();

    // init tabs
    $('.menu .item', $passportUpdate).tab();

    // fill user details once
    if (userAuthed) {
        // using userhash is insecure
        //$("#user_hash").val(userHash);
        if (userName)
            $("#story-author").val(userName).prop('disabled', true);
        $(".hide-authed").hide();
    } else {
        $(".social-login").on('click', function () {
            window.location = "/auth/" + $(this).data('provider');
        });
    }
});

var gsvlock;

function fillPassport(church) {
    _debug(church);
    currentChurch = church;
    gsvlock = true;
    fillSocial();
    resetEdit();
    clearAndFill();
    $passportUpdate.hide();
    var churchId = currentChurch.extID;

    $passportUpdateButtonWrapper.off('click').on('click', togglePassportUpdateForm);
    $(".passport-stories-wrapper").show();
    $passportUpdate.hide();

    $(".passport-update-button").html("dodaj swoje wspomnienie lub obraz");
    if (church.media && church.media.length > 0) {
        $churchStoriesTitle.html("Wspomnienia dodane przez użytkowników").show();
    }
    else {
        $churchStoriesTitle.hide();
    }

    initPassportGallery(churchId);
    initStoryGallery();
}

function resetEdit() {
    var $elem = $(".editable");
    $(".passport-data-edit", $elem).hide();
    $(".passport-data-data", $elem).show();
    $(".passport-data-help", $elem).hide();
    $(".passport-data-actions .passport-value-edit-icon", $elem).show();
    $(".passport-data-actions .passport-value-save-icon", $elem).hide();
}

function initFieldUpdates() {
    // bind events
    $(".passport-value-edit-icon").on('click', toggleEdit);
}

function clearAndFill() {
    // clear form
    $("td.passport-value .passport-data-data").empty();
    // filling form
    var churchId = currentChurch.extID;
    $(".church-name").html(currentChurch.name);
    $(".passport-value[data-field=address] .passport-data-data").html(currentChurch.address.unfolded);
    var churchStart = currentChurch.constructionStart ? currentChurch.constructionStart : "";
    var churchEnd = currentChurch.constructionEnd ? currentChurch.constructionEnd : "";
    var constructionValue = churchStart == churchEnd ? churchStart : churchStart + " - " + churchEnd;
    $(".passport-value[data-field=years] .passport-data-data").html(constructionValue);
    var churchArchitects = '';
    if (typeof currentChurch.architects === "string") {
        churchArchitects = currentChurch.architects;
    } else {
        var arcarr = [];
        if (currentChurch.architects && currentChurch.architects.length > 0)
            $(currentChurch.architects).each(function (a, item) {
                arcarr.push(item.name);
            });
        churchArchitects = arcarr.join(', ');
    }
    $(".passport-value[data-field=architects] .passport-data-data").html(churchArchitects);
    var website = currentChurch.website ? currentChurch.website : "";
    if (website != '')
        $(".passport-value[data-field=website] .passport-data-data").html('<a target="_blank" href="' + website + '">' + website + '</a>');
    else
        $(".passport-value[data-field=website] .passport-data-data").html(null);

    $(".passport-data-data").each(function (a, item) {
        var $item = $(item), $helper = $(".passport-data-help", $item.parent());
        if ($item.html() == '')
            $helper.show();
        else
            $helper.hide();
    });
    // fill in new story data
    $("#story-church-id").val(churchId);
}

function fillSocial() {
    $(".social-wrapper").empty().html(
        "<div class='fb-like' data-href='" + window.location + "' data-layout='button_count' data-action='like'" +
        " data-show-faces='true' data-share='false'></div>" +
        "<a href='https://twitter.com/share' class='twitter-share-button' data-lang='pl'>Tweetnij</a>"
    );
    try {
        if (typeof FB !== 'undefined')
            FB.XFBML.parse();
        if (typeof twttr !== 'undefined')
            twttr.widgets.load();
    } catch (e) {
        _debug('error in social init: ' + e);
    }
}

function togglePassportUpdateForm() {
    $(".passport-stories-wrapper").fadeToggle(1000);
    $passportUpdate.fadeToggle(1000);
    $("#story-title,#story-year").val("");
    $("#story-text").html("");
    $passportWrapper.modal('refresh');
}

function toggleEdit(ev) {
    var elem = ev != null && ($(ev).hasClass('passport-value-save-icon') || $(ev).hasClass('passport-data-edit')) ? ev : this;
    var $elem = $(elem).closest('.editable') || $(".church-name");
    _debug($elem);
    // special case for church name
    $elem.toggleClass('editing');
    if ($elem.hasClass('editing')) {
        if ($elem.hasClass('editable')) {
            var $currentDataElem = $(".passport-data-data", $elem);
            $currentDataElem.hide();
            $(".passport-data-actions .passport-value-edit-icon", $elem).hide();
            $(".passport-data-help", $elem).hide();
            $(".passport-data-actions .passport-value-save-icon", $elem).show();
            switch ($elem.data('field')) {
                case 'architects' :
                case 'address' :
                    $(".passport-data-edit", $elem).val($currentDataElem.html()).show().focus();
                    break;
                case 'website' :
                    $(".passport-data-edit", $elem).val(currentChurch.website).show().focus();
                    break;
                case 'years' :
                    $(".passport-data-edit", $elem).show();
                    $(".passport-data-edit[name=constructionStart]", $elem).val(currentChurch.constructionStart).focus();
                    $(".passport-data-edit[name=constructionEnd]", $elem).val(currentChurch.constructionEnd);
                    break;
                default :
                    _debug('huh?');
            }
        }
    } else {
        if ($elem.hasClass('editable')) {
            var $currentDataElem = $(".passport-data-edit", $elem);
            $currentDataElem.hide();
            $(".passport-data-data", $elem).show();
            $(".passport-data-actions .passport-value-edit-icon", $elem).show();
            $(".passport-data-actions .passport-value-save-icon", $elem).hide();
            switch ($elem.data('field')) {
                case 'years':
                case 'architects':
                case 'website':
                    $.extend(currentChurch, $currentDataElem.serializeObject());
                    break;
                case 'address' :
                    currentChurch.address.unfolded = $currentDataElem.val();
                    break;
                default :
                    _debug("huh??");
            }
            clearAndFill();
        }
    }
}

var commonFieldApi = {
    action: 'update passport field',
    method: 'POST',
    // todo: change to notification
    onSuccess: function (data) {
        _debug(data);
        toggleEdit($(this));
    },
    onError: function (data) {
        _debug(data);
        toggleEdit($(this));
    },
    beforeSend: adjustFieldSettings
};
var iconFieldApi = {on : 'click'};
$.extend(iconFieldApi, commonFieldApi);
var inputFieldApi = {on : 'now'};
$.extend(inputFieldApi, commonFieldApi);

function initUpdatePassportApi() {

    $(".submit.button", $passportUpdate).off('click').api({
        on: 'click',
        action: 'add new story',
        method: 'POST',
        onSuccess: function (data) {
            _debug(data);
            togglePassportUpdateForm();
            notifyOk();
        },
        // onFailure ??
        onError: function (errorMessage) {
            //togglePassportUpdateForm();
            notifyError(errorMessage);
        },
        beforeSend: adjustSettings
    });
    $(".passport-value-save-icon").off('click').api(iconFieldApi);
    $("input.passport-data-edit").on('keypress',
        function (event) {
            var keycode = (event.keyCode ? event.keyCode : event.which);
            if (keycode == '13') {
                $(this).api(inputFieldApi);
            }
            event.stopPropagation();
        });
}

function adjustFieldSettings(settings) {
    var $elem = $(this).closest(".editable");
    _debug($elem);
    settings.urlData = {
        field: $elem.data('field')
    };
    settings.data = {
        extID: currentChurch.extID
    };
    $.extend(settings.data, $("input", $elem).serializeObject());
    return settings;
}

function adjustSettings(settings) {
    var isStoryTab = $(".ui.tab.active", $passportUpdate).data('tab') == 'add-story';
    if (isStoryTab)
        settings.action = 'add church story';
    else
        settings.action = 'add church images';
    var data = {};
    var entity;
    var $entityForm = $(".ui.tab.active form", $passportUpdate);
    entity = $entityForm.data('entity');
    $.extend(data, $entityForm.serializeObject());
    $.extend(data, $('.common-form').serializeObject());
    data.entity = entity;
    settings.data = data;
    return settings;
}

function notifyOk(message) {
    $("#new-story-success-message").html(message).fadeIn('slow');
}

function notifyError(message) {
    $("#new-story-error-message").html(message).fadeIn('slow');
}

function initPassportGallery(id) {
    $passportGallery.empty().api({
        action: 'get church images',
        on: 'now',
        urlData: {id: id},
        onSuccess: doPassportGallery
    });
}

function doPassportGallery(data) {

    $passportCurrentImage = $("<div class='passport-current-image gallery-unit' />").hide();
    $passportCurrentImage.appendTo($passportGallery);
    $passportGalleryThumbs = $("<div class='passport-gallery-thumbs gallery-unit' />").hide();
    $passportGalleryThumbs.appendTo($passportGallery);

    $passportGallery.on('galleryready', function () {
        _debug('gallery ready');
        if ($('img', $passportGalleryThumbs).length == 0)
            $('.gallery-unit').hide();
        else
            $('.gallery-unit').show();
        $passportWrapper.modal('refresh');
    });

    appendGSV();

    $(data).each(function (a, image) {
        pushGalleryThumb($("<img>").attr({src: getThumb(image.path)})
            .data('big-src', image.path));
    });
}

function getThumb(resourceName) {
    var splitURI = resourceName.split('/');
    var name = splitURI.pop();
    var thumbSplit = name.split('.');
    var ext = thumbSplit.pop();
    var thumbName = thumbSplit.join('.') + '_thumb.' + ext;
    splitURI.push(thumbName);
    return splitURI.join('/');
}

function pushGalleryThumb($elem) {
    $passportGalleryThumbs.append($("<div class='passport-thumb-image'>").append($elem));
}

function appendGSV() {

    var center = new google.maps.LatLng(currentChurch.address.geometry[0], currentChurch.address.geometry[1]);
    var streetViewService = new google.maps.StreetViewService();
    var maxDistanceFromCenter = 50; //meters
    var galleryHeight = 480;
    var galleryWidth = "860px";
    if (gsvlock)
        streetViewService.getPanoramaByLocation(center, maxDistanceFromCenter, function (streetViewPanoramaData, status) {
            if (status === google.maps.StreetViewStatus.OK) {
                //var coords = getCoordsString(currentChurch.address.geometry);
                var lat = streetViewPanoramaData.location.latLng.lat();
                var lng = streetViewPanoramaData.location.latLng.lng();
                var coords = lat + ',' + lng;
                var heading = getHeading(toRad(lat), toRad(lng), toRad(center.lat()), toRad(center.lng()));
                var url = "https://www.google.com/maps/embed/v1/streetview?location=" + coords + "&key=" + googleApiKey + "&heading=" + heading;
                var gsvElem = "<iframe width='" + galleryWidth + "' height='" + galleryHeight +
                    "' frameborder='0' style='border:0'" +
                    " src='" + url + "'></iframe>";
                _debug(gsvElem);
                $(gsvElem).appendTo($passportCurrentImage);
                var $gsvThumbElem = $('<img>').attr({src: '/assets/uploads/000000/gsv_thumb.png'})
                    .data('big-src', '#gsv-special');
                pushGalleryThumb($gsvThumbElem);
                $passportCurrentImage.trigger('galleryready');
            } else {
                _debug('street view returned : ' + status);
                $passportCurrentImage.trigger('galleryready');
            }
        });
    gsvlock = false;
}

var STORY_PICS_COUNT = 8;
function initStoryGallery() {
    $newStoryGallery.empty();
    for (var i = 1; i <= STORY_PICS_COUNT; i++) {
        $("<img>").attr({
            src: "/assets/images/church_story_" + i + ".png",
            id: "si_" + i,
            height: 100,
            width: 100
        })
            .addClass("ui image new-story-cover")
            .on("click", selectStoryImage)
            .appendTo($newStoryGallery);
    }
    var random = (Math.ceil(Math.random() * STORY_PICS_COUNT + 0.5));
    $("#si_" + random).click();
}

function selectStoryImage() {
    var id = getId($(this));
    $(".image", $newStoryGallery).removeClass("active");
    $("#si_" + id, $newStoryGallery).addClass("active");
    $("#story-cover-id", $passportUpdate).val(id);
}