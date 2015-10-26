var $churchStoriesTitle, $churchStories, $passportUpdateButtonWrapper, $passportGalleryThumbs, $passportGalleryControls, $passportGalleryGSV,
    $passportUpdate, $newStoryGallery, $passportGallery, $passportWrapper;

var $passportCurrentImage;

var $overlayArrowUp, $overlayArrowDown;

// todo: remove crutch
var DEF_IMAGE_DESC = 'Autor: Igor Snopek';

$(document).ready(function () {
    $passportWrapper = $(".passport-wrapper");
    $passportGallery = $(".passport-gallery");

    $churchStoriesTitle = $(".passport-stories-title");
    $churchStories = $(".church-stories");

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

    // add button hover

    $passportUpdateButtonWrapper.hover(function()
    {
        $(this).addClass('hover');
    }, function()
    {
        $(this).removeClass('hover');
    })

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

    $(".passport-update-button").html("DODAJ WSPOMNIENIE LUB ZDJĘCIE");
    if (church.media && church.media.length > 0) {
        $churchStoriesTitle.html("Wspomnienia dodane przez użytkowników").show();
        $churchStories.empty();
        inhabitThumbs($churchStories, 'story', church.media);
        $churchStories.isotope();
        bindThumbEvents($churchStories, 'story');
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
    $(".passport-value-edit-icon", $elem).show();
}

function initFieldUpdates() {
    // bind events
    //$(".passport-value-edit-icon").on('click', toggleEdit);
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
    var constructionYearsHaveIssue = !churchStart || !churchEnd;
    var constructionValue =
        churchStart == churchEnd ?
            churchStart :
            (((churchStart) ? churchStart : 'bd.')
            + " - " +
            ((churchEnd) ? churchEnd : 'bd.'));
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
    if (constructionYearsHaveIssue) {
        $(".passport-value-edit-icon", $(".passport-value[data-field=years]")).show();
    }
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
    //$(".passport-stories-wrapper").fadeToggle(1000);
    $passportUpdate.fadeIn(1000);
    $("#story-title,#story-year").val("");
    $("#story-text").html("");
    $passportWrapper.modal('refresh');
    _scrollTo($passportWrapper.parent(), $(".story-submit", $passportUpdate), 1000);
}

//function toggleEdit(ev) {
//    var elem = ev != null && ($(ev).hasClass('passport-value-save-icon') || $(ev).hasClass('passport-data-edit')) ? ev : this;
//    var $elem = $(elem).closest('.editable') || $(".church-name");
//    _debug($elem);
//    // special case for church name
//    $elem.toggleClass('editing');
//    if ($elem.hasClass('editing')) {
//        if ($elem.hasClass('editable')) {
//            var $currentDataElem = $(".passport-data-data", $elem);
//            $currentDataElem.hide();
//            $(".passport-data-actions .passport-value-edit-icon", $elem).hide();
//            $(".passport-data-help", $elem).hide();
//            $(".passport-data-actions .passport-value-save-icon", $elem).show();
//            switch ($elem.data('field')) {
//                case 'architects' :
//                case 'address' :
//                    $(".passport-data-edit", $elem).val($currentDataElem.html()).show().focus();
//                    break;
//                case 'website' :
//                    $(".passport-data-edit", $elem).val(currentChurch.website).show().focus();
//                    break;
//                case 'years' :
//                    $(".passport-data-edit", $elem).show();
//                    $(".passport-data-edit[name=constructionStart]", $elem).val(currentChurch.constructionStart).focus();
//                    $(".passport-data-edit[name=constructionEnd]", $elem).val(currentChurch.constructionEnd);
//                    break;
//                default :
//                    _debug('huh?');
//            }
//        }
//    } else {
//        if ($elem.hasClass('editable')) {
//            var $currentDataElem = $(".passport-data-edit", $elem);
//            $currentDataElem.hide();
//            $(".passport-data-data", $elem).show();
//            $(".passport-data-actions .passport-value-edit-icon", $elem).show();
//            $(".passport-data-actions .passport-value-save-icon", $elem).hide();
//            switch ($elem.data('field')) {
//                case 'years':
//                case 'architects':
//                case 'website':
//                    $.extend(currentChurch, $currentDataElem.serializeObject());
//                    break;
//                case 'address' :
//                    currentChurch.address.unfolded = $currentDataElem.val();
//                    break;
//                default :
//                    _debug("huh??");
//            }
//            clearAndFill();
//        }
//    }
//}

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
var iconFieldApi = {on: 'click'};
$.extend(iconFieldApi, commonFieldApi);
var inputFieldApi = {on: 'now'};
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

var $galleryItems = [];

function initPassportGallery(id) {
    $galleryItems = [];
    $passportGallery.empty();
    $passportGallery.api({
        action: 'get church images',
        on: 'now',
        urlData: {id: id},
        onSuccess: doPassportGallery
    });
}

function doPassportGallery(galldata) {
    $passportCurrentImage = $("<div class='passport-current-image gallery-unit' />").hide();
    $passportCurrentImage.appendTo($passportGallery);
    $passportGalleryGSV = $("<div class='passport-gallery-gsv gallery-unit' />").hide();
    $passportGalleryGSV.appendTo($passportGallery);
    $passportGalleryThumbs = $("<div class='passport-gallery-thumbs gallery-unit' />").hide();
    $passportGalleryThumbs.appendTo($passportGallery);
    $passportGalleryControls =
        $("<div class='passport-gallery-controls gallery-unit passthrough' />")
            .append($("<div class='passport-gallery-control gallery-thumb-arrow-up' />"))
            .append($("<div class='passport-gallery-control passthrough medium-fill' />"))
            .append($("<div class='passport-gallery-control bubble-event-control' />"))
            .append($("<div class='passport-gallery-control passthrough medium-fill' />"))
            .append($("<div class='passport-gallery-control gallery-thumb-arrow-down' />"))
            .hide().appendTo($passportGallery);

    $passportGallery.off('galleryready').on('galleryready', function () {
        _debug('gallery ready');

        $(galldata).each(function (a, image) {
            pushGalleryThumb({
                src: getThumb(image.path),
                big: image.path,
                id: image.id,
                desc: image.description
            });
        });
        //$("<div class='gallery-thumb-arrow-down' />").appendTo($passportGalleryThumbs);

        $overlayArrowUp = $(".gallery-thumb-arrow-up")
            .off('click').on('click', galleryUp);
        $overlayArrowDown = $(".gallery-thumb-arrow-down")
            .off('click').on('click', galleryDown);
        $(".bubble-event-control").off('click').on('click', bubbleClick);
        var $thumbs = $('.passport-thumb-image:not(.empty,.inactive)');

        // populate with empty
        for (var i = 0; i < 5 - galldata.length ; i++)
            pushEmpty();

        var noGSV = $('.passport-thumb-image', $passportGalleryGSV).hasClass('inactive');
        if ($thumbs.length == 0 && noGSV)
            $('.gallery-unit').hide();
        else {
            // at least we have something to show
            $thumbs.off('click').on('click', function () {
                var $thisThumb = $(this);
                if ($thisThumb.hasClass('inactive'))
                    return;
                if (!$thisThumb.hasClass('active')) {
                    $thumbs.removeClass('active');
                    $thisThumb.addClass('active');
                    var $galleryItem = $galleryItems[$thisThumb.data('id')];
                    $passportCurrentImage.html($galleryItem);

                    var desc = $thisThumb.data('description');
                    if (desc != 'gsv') {
                        if (typeof desc === 'undefined' || desc.trim())
                        {
                            desc = DEF_IMAGE_DESC;
                        }
                        $passportCurrentImage.append(
                            $("<div class='passport-gallery-large-description' />").html(
                                $("<span class='passport-gallery-large-description-content' />").html(desc)
                            ).hide()
                        ).hover(function () {
                                $(".passport-gallery-large-description").fadeIn(600);
                            }, function () {
                                $(".passport-gallery-large-description").fadeOut(600);
                            });
                    }
                }
            });
            //// add arrows and scroll behaviour
            //$passportGalleryControls.app
            $($thumbs[0]).trigger('click');
            // scroll to the first thumb

            if (galldata.length > 0)
            {
                while (!$(".passport-thumb-image", $passportGalleryThumbs).eq(2).hasClass('normal'))
                  galleryUp();
            }

            if (noGSV)
                thumbscroll();
            $('.gallery-unit').show();
        }

        $passportWrapper.modal('refresh');
        $passportWrapper.trigger('passportready');
    });

    //$("<div class='gallery-thumb-arrow-up' />").appendTo($passportGalleryThumbs);
    appendGSV();
}

function pushEmpty()
{
    pushGalleryThumb({
        src: '/assets/uploads/000000/no_church_thumb.jpg',
        id: null
    });
}

function bubbleClick(e) {
    thumbscroll();
}

function galleryUp() {
    //var scrollPos = $passportGalleryThumbs.scrollTop();
    //$passportGalleryThumbs.scrollTop(scrollPos - 80);
    $(".passport-thumb-image:first", $passportGalleryThumbs)
        .insertAfter($(".passport-thumb-image:last", $passportGalleryThumbs));
    thumbscroll();
}

function galleryDown() {
    //var scrollPos = $passportGalleryThumbs.scrollTop();
    //$passportGalleryThumbs.scrollTop(scrollPos + 80);
    $(".passport-thumb-image:last", $passportGalleryThumbs)
        .insertBefore($(".passport-thumb-image:first", $passportGalleryThumbs));
    thumbscroll();
}

function thumbscroll() {
    //$(".passport-thumb-image.active", $passportGalleryThumbs).removeClass('active');
    $passportGalleryThumbs.find('div').eq(2).trigger('click');

}

function getThumb(resourceName) {
    var splitURI = resourceName.split('/');
    var name = splitURI.pop();
    var thumbSplit = name.split('.');
    var ext = thumbSplit.pop();
    var thumbName = thumbSplit.join('.') + '_thumb_ed.' + ext;
    splitURI.push(thumbName);
    return splitURI.join('/');
}

function pushGalleryThumb(thumbData) {

    if (thumbData && thumbData.src) {
        var $thisThumb = $("<div class='passport-thumb-image'>");

        var big = thumbData.big;
        if (thumbData.id == null) {
            $thisThumb.addClass('empty inactive')
                .appendTo($passportGalleryThumbs)
                .css('background', 'transparent url(' + thumbData.src + ') no-repeat center center');
        }
        else if (thumbData.id == 'gsv') {
            $thisThumb.addClass('gsv')
                .data({id: thumbData.id});
            if (!big)
                $thisThumb.addClass('inactive');
            $thisThumb
                .appendTo($passportGalleryGSV);

            $galleryItems[thumbData.id] = big; // having gsv iframe here
        }
        else {
            $galleryItems[thumbData.id] = $("<div class='passport-gallery-large-image'/>")
                .css({
                    background: 'black url(' + big + ') center center no-repeat ',
                    'background-size': 'contain'
                });
            $thisThumb
                .css('background', 'transparent url(' + thumbData.src + ') no-repeat center center')
                .data({id: thumbData.id, description: thumbData.desc})
                .addClass('normal')
                .appendTo($passportGalleryThumbs);
        }
    }
}

function appendGSV() {

    var center = new google.maps.LatLng(currentChurch.address.geometry[0], currentChurch.address.geometry[1]);
    var streetViewService = new google.maps.StreetViewService();
    var maxDistanceFromCenter = 50; //meters
    var galleryHeight = 480;
    var galleryWidth = 860;
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
                var $gsv = $(gsvElem);
                $gsv.appendTo($passportCurrentImage);
                var gsvThumbData = {
                    src: '/assets/uploads/000000/gsv_thumb.png',
                    big: $gsv,
                    id: 'gsv',
                    desc: 'gsv'
                };
                pushGalleryThumb(gsvThumbData);
                $passportCurrentImage.trigger('galleryready');
            } else {
                _debug('street view returned : ' + status);
                pushGalleryThumb({
                    src: '/assets/uploads/000000/gsv_thumb_inactive.png',
                    big: "",
                    id: 'gsv',
                    desc: 'gsv'
                });
                $passportCurrentImage.trigger('galleryready');
            }
        });
    gsvlock = false;
}

var STORY_PICS_COUNT = 8;
function initStoryGallery() {
    var random = (Math.round(Math.random() * STORY_PICS_COUNT + 0.5));
    $("#story-cover-id", $passportUpdate).val(random);
}
