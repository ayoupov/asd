var $churchStoriesTitle, $churchStories, $passportUpdateButtonWrapper,
    $passportGalleryThumbs, $passportGalleryControls, $passportGalleryGSV,
    $passportUpdate, $passportGallery, $passportWrapper, $passportSuggestForm,
    $reportMistakesWrapper;

var $passportCurrentImage, $overlayArrowUp, $overlayArrowDown;

var globalTOSConfirmed = Cookies.get("tos-confirmed");

// todo: remove crutch
var DEF_IMAGE_DESC = 'Autor: Igor Snopek';

$(document).ready(function () {
    $passportWrapper = $(".passport-wrapper");
    $passportGallery = $(".passport-gallery", $passportWrapper);

    $churchStoriesTitle = $(".passport-stories-title", $passportWrapper);
    $churchStories = $(".church-stories", $passportWrapper);

    $passportUpdate = $(".passport-update", $passportWrapper);
    //$newStoryGallery = $(".story-cover-gallery");
    $passportUpdateButtonWrapper = $(".passport-update-button-wrapper", $passportWrapper);

    $passportSuggestForm = $(".passport-suggest", $passportWrapper);

    $reportMistakesWrapper = $(".report-mistakes-wrapper", $passportWrapper).hide();

    // fill user details once
    if (userAuthed) {
        // using userhash is insecure
        //if (userName)
        //    $(".uploaded-by").val(userName).prop('disabled', true);
        $(".hide-authed").hide();
        $(".not-you-button").on('click', function () {
            window.location = "/auth/logout";
        });
    } else {
        $(".social-login").on('click', function () {
            window.location = "/auth/" + $(this).data('provider');
        });
    }

    initUpdatePassportApi();
    initPassportUI();
});

var gsvlock, churchMedia, churchMediaIndex;

function fillPassport(church) {
    _debug(church);
    currentChurch = church;
    gsvlock = true;
    fillSocial();
    resetEdit();
    clearAndFillDataTable();
    $passportUpdate.hide();
    var churchId = currentChurch.extID;
    $("input.church-id-hidden").val(currentChurch.extID);

    $passportUpdateButtonWrapper.off('click').on('click', showPassportUpdateForm).show();
    $(".passport-stories-wrapper").show();

    $passportUpdate.hide();

    $(".passport-data-help").off('click').on('click', showPassportSuggestForm);
    $passportSuggestForm.hide();

    churchMedia = filterMedia(church.media);
    churchMediaIndex = 0;
    $churchStories.empty();
    if (churchMedia && churchMedia.length > 0) {
        $churchStoriesTitle.html("Wspomnienia dodane przez użytkowników").show();
        inhabitNext();
        $churchStories.isotope();
        bindThumbEvents($churchStories, 'story');
    }
    else {
        $churchStoriesTitle.hide();
    }

    initPassportGallery(churchId);
    initStoryRandomPicture();
}

function filterMedia(data) {
    var res = [];
    $(data).each(function (a, item) {
        if (item.approvedDT)
            res.push(item);
    });
    return res;
}

function inhabitNext() {
    if ($(".extra-story", $churchStories).length > 0)
        $churchStories.isotope('remove', $(".extra-story"));
    var prevChurchMediaIndex = churchMediaIndex;
    churchMediaIndex += (prevChurchMediaIndex == 0) ? 7 : 4;
    churchMediaIndex = Math.min(churchMedia.length, churchMediaIndex);
    var lastItem = inhabitThumbs($churchStories, 'story', churchMedia.slice(prevChurchMediaIndex, churchMediaIndex));
    var whatsleft = churchMedia.length - churchMediaIndex;
    if (whatsleft > 0) {
        var $more = $("<div/>").attr('id', 'more-story-thumb').addClass('extra-story story thumb center-more white').append(
            $('<div/>').addClass('more-wrapper grayish-bordered').append(
                $('<div/>').addClass('more').html('Więcej ' + whatsleft)
            )
        );
        $more.insertAfter(lastItem);
        $more.off('click').on('click', inhabitNext);
        $churchStories.isotope('appended', $more);
    }
    var visibleStories = $('.story', $churchStories).length;
    // | 0 -- gets integer result of division
    $churchStories.css('height', (350 * (((visibleStories - 1) / 4 | 0) + 1)) + 'px');
}

function initPassportUI() {

    $(".close-button", $passportWrapper).on('click', function () {
        $passportWrapper.modal('hide');
    });

    $passportWrapper.on('passportready', function()
    {
        suggestionUIInit();

        $reportMistakesWrapper.show().on('click', function () {
            showPassportSuggestForm();
            suggestionDropdown.find("[data-value='name']").trigger("click");
            suggestionChangeFunc('name');
        });
    });

    $passportWrapper.modal({
        onHidden: function () {
            removeHash();
            setMapMode();
        }
    });

    // init tabs
    $('.menu .item', $passportUpdate).tab();

    // init cancel buttons
    $('.cancel-button', $passportUpdate).on('click', function () {
        hidePassportUpdateForm();
    });

    $('.passport-update-message').hide();

    fileInputTweak();

    $(".upload-confirmation").on('change', function () {
        var checked = $(this).is(':checked');
        if (checked) {
            globalTOSConfirmed = true;
            Cookies.set("tos-confirmed", "true", {expires: 365});
        }
        else {
            globalTOSConfirmed = false;
            Cookies.remove("tos-confirmed");
        }
    })
}

function resetEdit() {
    var $elem = $(".editable");
    $(".passport-data-edit", $elem).hide();
    $(".passport-data-data", $elem).show();
    $(".passport-data-help", $elem).hide();
    $(".passport-value-edit-icon", $elem).show();
}

function getCurrentForm() {
    var $thisTab = $(".ui.tab.active", $passportUpdate);
    return $(".entity-form", $thisTab);
}

function clearAndFillDataTable() {
    // clear datatable
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

function showPassportUpdateForm() {
    $(".passport-stories-wrapper").fadeToggle(800);
    $passportUpdate.slideUp(800);
    $passportUpdate.fadeIn(800, function () {
        $passportWrapper.modal('refresh');
        _scrollTo($passportWrapper.parent(), $("#passportmenu-bottom"), 900);
    });

    resetForm(getCurrentForm());

    $passportWrapper.modal('refresh');
    //_scrollTo($passportWrapper.parent(), $("#passportmenu-bottom"), 900);
}

function hidePassportUpdateForm() {
    $(".passport-stories-wrapper").fadeToggle(800);
    $passportUpdate.fadeOut(800);

    $passportWrapper.modal('refresh');
    //_scrollTo($passportWrapper.parent(), $(".entity-submit", $passportUpdate), 900);
    _scrollTo($passportWrapper.parent(), $("#passportmenu-bottom"), 900);
}

function resetForm($form) {
    // delete all form image blocks except first ones
    resetFileInputs($form);
    $form[0].reset();
    // fill in new story data
    $("input.church-id-hidden").val(currentChurch.extID);
    if (userAuthed) {
        if (userName)
            $(".uploaded-by", $form).val(userName).prop('disabled', true);
    }
    initStoryRandomPicture();
    if (globalTOSConfirmed)
        $('.upload-confirmation', $form).attr('checked', 'checked');
    else
        $('.upload-confirmation', $form).removeAttr('checked');
}

function initUpdatePassportApi() {

    //apply a custom submit function for the form
    $(".entity-submit", $passportUpdate).off('click').on('click', customUpdateWithImages);

    var fieldUpdateApi = {
        method: 'POST',
        onSuccess: function (data) {
            notifySuggestionOk("Dziękujemy za pomoc, czytamy wszystkie<br>komentarze i na bierząco aktualizujemy dane", hidePassportSuggestForm);
        },
        onError: function (errorMessage) {
            notifySuggestionError(errorMessage);
        },
        beforeSend: adjustSuggestionSettings
    };

    $(".entity-submit", $passportSuggestForm).off('click').api(
        $.extend({on: 'click'}, fieldUpdateApi)
    );
    $passportSuggestForm.on("keypress", function (event) {
        return event.keyCode != 13;
    });
}

var customUpdateWithImages = function (e) {

    //disable the default form submission
    e.preventDefault();

    var $thisTab = $(".ui.tab.active", $passportUpdate);
    var isStoryTab = $thisTab.data('tab') == 'add-story';
    var url = (isStoryTab) ? '/church/story' : '/church/images';
    var entity;
    var $entityForm = getCurrentForm();


    //grab all form data
    var formData = new FormData();
    var data = $entityForm.serializeObject();
    for (var key in data) {
        formData.append(key, data[key]);
    }
    // append files
    $("input:file[name]", $entityForm).each(function (a, item) {
        formData.append($(item).attr('name'), item.files[0]);
    });

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            resetForm($entityForm);
            notifyOk(data.message, hidePassportUpdateForm);
        },
        error: function (errorMessage) {
            //showPassportUpdateForm();
            notifyError(errorMessage);
        }
    });

    return false;

};

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
    $("<div class='passport-gallery-controls gallery-unit passthrough' />")
        .append($("<div class='passport-gallery-control gallery-thumb-arrow-up' />"))
        .append($("<div class='passport-gallery-control passthrough medium-fill' />"))
        .append($("<div class='passport-gallery-control bubble-event-control' />"))
        .append($("<div class='passport-gallery-control passthrough medium-fill' />"))
        .append($("<div class='passport-gallery-control gallery-thumb-arrow-down' />"))
        .append($("<div class='passport-gallery-control gallery-thumb-arrow-down-overlay' />"))
        .append($("<div class='passport-gallery-control gallery-thumb-arrow-up-overlay' />"))
        .hide().appendTo($passportGallery);
    $passportGalleryControls = $(".passport-gallery-controls", $passportGallery);

    $passportGallery.off('galleryready').on('galleryready', function () {
        _debug('gallery ready');

        // populate with empty
        for (var i = 0; i < 5 - galldata.length; i++)
            pushEmpty();

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
                    var isGSV = $thisThumb.hasClass('gsv');
                    if (!isGSV)
                        $galleryItem.off('click').on('click', galleryDown);

                    if (isGSV)
                        $passportGalleryControls.removeClass('passthrough');
                    else
                        $passportGalleryControls.addClass('passthrough');

                    $passportCurrentImage.html($galleryItem);

                    var desc = $thisThumb.data('description');
                    if (!isGSV) {
                        if (typeof desc === 'undefined' || desc == null || desc.trim() == '') {
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

            $thumbs.eq(0).trigger('click');
            // scroll to the first thumb

            if (galldata.length > 0) {
                while (!$(".passport-thumb-image", $passportGalleryThumbs).eq(2).hasClass('normal'))
                    galleryUp();
            }

            if (noGSV)
                thumbscroll();
            $('.gallery-unit').show();

            // add on controls click

            $passportGalleryControls.off('click').on('click', function () {
                // assuming that this event happens only when clicked while GSV is active
                $passportGalleryControls.addClass('passthrough');
                if (noGSV)
                    return;
                //$(".passport-thumb-image", $passportGalleryThumbs).eq(2).addClass('active');
                thumbscroll();
            })
        }

        $passportWrapper.modal('refresh');
        $passportWrapper.trigger('passportready');
    });

    appendGSV();
}

function pushEmpty() {
    pushGalleryThumb({
        src: '/assets/images/passport/no_church_thumb.jpg',
        id: null
    });
}

function bubbleClick(e) {
    thumbscroll();
}

function galleryUp() {
    if ($('.passport-thumb-image:not(.empty,.inactive,.gsv)').length > 0)
        do {
            $(".passport-thumb-image:last", $passportGalleryThumbs)
                .insertBefore($(".passport-thumb-image:first", $passportGalleryThumbs));
        }
        while (!$(".passport-thumb-image", $passportGalleryThumbs).eq(2).hasClass('normal'));
    thumbscroll();
}

function galleryDown() {
    if ($('.passport-thumb-image:not(.empty,.inactive,.gsv)').length > 0)
        do {
            $(".passport-thumb-image:first", $passportGalleryThumbs)
                .insertAfter($(".passport-thumb-image:last", $passportGalleryThumbs));
        }
        while (!$(".passport-thumb-image", $passportGalleryThumbs).eq(2).hasClass('normal'));

    thumbscroll();
}

function thumbscroll() {
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
            $thisThumb.hover(function () {
                $(this).addClass('hover');
            }, function () {
                $(this).removeClass('hover');
            });
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
    var distances = [100, 50];
    var galleryHeight = 480;
    var galleryWidth = 860;

    function approximateGSVDistances() {
        var distance = distances.pop();
        streetViewService.getPanoramaByLocation(center, distance, function (streetViewPanoramaData, status) {
            if (status === google.maps.StreetViewStatus.OK) {
                distances = [];
                var lat = streetViewPanoramaData.location.latLng.lat();
                var lng = streetViewPanoramaData.location.latLng.lng();
                var coords = lat + ',' + lng;
                var heading = getHeading(toRad(lat), toRad(lng), toRad(center.lat()), toRad(center.lng()));
                var url = "https://www.google.com/maps/embed/v1/streetview?location=" + coords
                    + "&key=" + googleApiKey + "&heading=" + heading + "&fov=100";
                var gsvElem = "<iframe width='" + galleryWidth + "' height='" + galleryHeight +
                    "' frameborder='0' style='border:0'" +
                    " src='" + url + "'></iframe>";
                _debug(gsvElem);
                var $gsv = $(gsvElem);
                $gsv.appendTo($passportCurrentImage);
                var gsvThumbData = {
                    src: '/assets/images/passport/gsv_thumb.png',
                    big: $gsv,
                    id: 'gsv',
                    desc: 'gsv'
                };
                pushGalleryThumb(gsvThumbData);
                gsvlock = false;
                $passportCurrentImage.trigger('galleryready');
            } else {
                if (!distances.length) {
                    _debug('street view returned : ' + status);
                    pushGalleryThumb({
                        src: '/assets/images/passport/gsv_thumb_inactive.png',
                        big: "",
                        id: 'gsv',
                        desc: 'gsv'
                    });
                    $passportCurrentImage.trigger('galleryready');
                }
                else approximateGSVDistances();
            }
        });
    }

    approximateGSVDistances();
    gsvlock = false;
}

var STORY_PICS_COUNT = 8;
function initStoryRandomPicture() {
    var random = (Math.round(Math.random() * STORY_PICS_COUNT + 0.5));
    $("#story-cover-id", $passportUpdate).val(random);
}
