var $churchStoriesTitle, $churchStories, $passportUpdateButtonWrapper, $passportGalleryThumbs, $passportGalleryControls, $passportGalleryGSV,
    $passportUpdate, $newStoryGallery, $passportGallery, $passportWrapper, $passportSuggestForm;

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

    $passportSuggestForm = $(".passport-suggest");

    // fill user details once
    if (userAuthed) {
        // using userhash is insecure
        //$("#user_hash").val(userHash);
        if (userName)
            $(".uploaded-by").val(userName).prop('disabled', true);
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
    clearAndFill();
    $passportUpdate.hide();
    var churchId = currentChurch.extID;

    $passportUpdateButtonWrapper.off('click').on('click', showPassportUpdateForm);
    $(".passport-stories-wrapper").show();
    $passportUpdate.hide();

    $(".passport-data-help").off('click').on('click', showPassportSuggestForm);
    $passportSuggestForm.hide();

    churchMedia = filterMedia(church.media);
    churchMediaIndex = 0;
    if (churchMedia && churchMedia.length > 0) {
        $churchStoriesTitle.html("Wspomnienia dodane przez użytkowników").show();
        $churchStories.empty();
        inhabitNext();
        $churchStories.isotope();
        bindThumbEvents($churchStories, 'story');
    }
    else {
        $churchStoriesTitle.hide();
    }

    initPassportGallery(churchId);
    initStoryGallery();
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
    $(".you-button").off('click').on('click', function () {
        $('#authored-by').val($('.uploaded-by').val());
    });

    // button
    $(".passport-update-button").html("DODAJ WSPOMNIENIE LUB ZDJĘCIE");
    $passportUpdateButtonWrapper.hover(function () {
        $(this).addClass('hover');
    }, function () {
        $(this).removeClass('hover');
    }).show();

    $(".close-button").on('click', function () {
        $passportWrapper.modal('hide');
    });
    $passportWrapper.modal({
        onHidden: function () {
            removeHash();
            setMapMode();
        }
    });

    // init tabs
    $('.menu .item', $passportUpdate).tab();

    $('.passport-update-message').hide();

    fileInputTweak();
}

function fileInputTweak() {
    var fileExtensionRange = '.png .jpg .jpeg .gif';
    var MAX_SIZE = 4; // MB

    $(document).on('change', '.btn-file :file', function () {
        var input = $(this);

        if (navigator.appVersion.indexOf("MSIE") != -1) { // IE
            var label = input.val();

            input.trigger('fileselect', [1, label, 0]);
        } else {
            var label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            var numFiles = input.get(0).files ? input.get(0).files.length : 1;
            var size = input.get(0).files[0].size;

            input.trigger('fileselect', [numFiles, label, size]);
        }
    });

    $('.btn-file :file').on('fileselect', function (event, numFiles, label, size) {
        var $attachmentName = $('#attachmentName');
        $attachmentName.attr('name', 'attachmentName'); // allow upload.

        var postfix = label.substr(label.lastIndexOf('.'));
        if (fileExtensionRange.indexOf(postfix.toLowerCase()) > -1) {
            if (size > 1024 * 1024 * MAX_SIZE) {
                notifyError('max size：<strong>' + MAX_SIZE + '</strong> MB.');

                $attachmentName.removeAttr('name'); // cancel upload file.
            } else {
                $('#_attachmentName').val(label);
            }
        } else {
            notifyError('Please, upload image with one of these extensions: <strong>' + fileExtensionRange + '</strong>');

            $attachmentName.removeAttr('name'); // cancel upload file.
        }
    });
}

function resetEdit() {
    var $elem = $(".editable");
    $(".passport-data-edit", $elem).hide();
    $(".passport-data-data", $elem).show();
    $(".passport-data-help", $elem).hide();
    $(".passport-value-edit-icon", $elem).show();
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
    $("#story-church-id,#photo-church-id,#suggestion-church-id").val(churchId);
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
    $passportUpdate.fadeIn(800);
    clearUpdateForm();

    $passportWrapper.modal('refresh');
    _scrollTo($passportWrapper.parent(), $(".entity-submit", $passportUpdate), 900);
}

function hidePassportUpdateForm() {
    $(".passport-stories-wrapper").fadeToggle(800);
    $passportUpdate.fadeOut(800);
    //clearUpdateForm();

    $passportWrapper.modal('refresh');
    _scrollTo($passportWrapper.parent(), $(".entity-submit", $passportUpdate), 900);
}

function showPassportSuggestForm() {
    if ($passportUpdate.is(":visible"))
        $passportUpdate.slideUp('slow');
    initSuggestForm($(this));
    var visibleAlready = $passportSuggestForm.is(":visible");
    //$passportUpdateButtonWrapper.slideUp('slow', function () {
    //$passportUpdateButtonWrapper.hide(function () {
    $passportUpdateButtonWrapper.hide();
        if (!visibleAlready) {
            $passportSuggestForm.fadeIn(300, focusOnField);
            _scrollTo($passportWrapper.parent(), $("#field-value", $passportSuggestForm), 900);
        }
    //});
    $passportWrapper.modal('refresh');
}

function hidePassportSuggestForm() {
    $(".passport-stories-wrapper").show('slow');
    $passportUpdateButtonWrapper.show();
    $passportSuggestForm.fadeOut(800);
    //initSuggestForm($(this));

    $passportWrapper.modal('refresh');
    _scrollTo($passportWrapper.parent(), $passportUpdateButtonWrapper, 900);
}

function focusOnField()
{
    $("#field-value", $passportSuggestForm).focus();
}

function initSuggestForm($elem) {
    var targetField = $elem.parent().data('field');
    var $field = $("#field-value", $passportSuggestForm);
    // select option
    $('select option[value="' + targetField + '"]', $passportSuggestForm).prop('selected', true);
    $("select.suggestion-field-select", $passportSuggestForm)
        .off('change')
        .on('change', function () {
            var v;
            var field = $(this).find(':selected').val();
            if (field == "website") {
                v = currentChurch.website;
            } else if (field == "name") {
                v = currentChurch.name;
            } else {
                var $e = $(".passport-data-data", $(".passport-value[data-field=" + field + "]"));
                v = $e.html();
            }
            // fill field value
            $field.val(v);
            if (field == "other")
                $field.attr('placeholder', 'Please leave any other suggestion on this church');
            else
                $field.attr('placeholder', '');
        }).trigger('change');
}

function clearUpdateForm() {
    $("#story-title,#story-year,#story-text,#authored-by,#photo-description").val("");
}

function initUpdatePassportApi() {

    $(".entity-submit", $passportUpdate).off('click').api({
        on: 'click',
        method: 'POST',
        onSuccess: function (data) {
            //_debug(data);
            clearUpdateForm();
            notifyOk("Odpowiemy Ci w ciągu 24 godzin", hidePassportUpdateForm);
        },
        // onFailure ??
        onError: function (errorMessage) {
            //showPassportUpdateForm();
            notifyError(errorMessage);
        },
        beforeSend: adjustSettings
    });

    var fieldUpdateApi = {
        method: 'POST',
        onSuccess: function (data) {
            //_debug(data);
            notifySuggestionOk("Thank you for your report", hidePassportSuggestForm);
        },
        // onFailure ??
        onError: function (errorMessage) {
            //showPassportUpdateForm();
            notifySuggestionError(errorMessage);
        },
        beforeSend: adjustSuggestionSettings
    };

    $(".entity-submit", $passportSuggestForm).off('click').api(
        $.extend({on: 'click'}, fieldUpdateApi)
    );
    //$passportSuggestForm.api(fieldUpdateApi);
    $passportSuggestForm.on("keypress", function (event) {
        return event.keyCode != 13;
    });
}

function adjustSettings(settings) {
    var $thisTab = $(".ui.tab.active", $passportUpdate);
    var isStoryTab = $thisTab.data('tab') == 'add-story';
    if (isStoryTab)
        settings.action = 'add church story';
    else
        settings.action = 'add church images';
    var data = {};
    var entity;
    var $entityForm = $(".entity-form", $thisTab);
    entity = $entityForm.data('entity');
    $.extend(data, $entityForm.serializeObject());
    $.extend(data, $('.common-form', $thisTab).serializeObject());
    data.entity = entity;
    settings.data = data;
    return settings;
}

function adjustSuggestionSettings(settings) {
    settings.action = 'update passport field';
    var fieldName = $(".suggestion-field-select", $passportSuggestForm).val();
    settings.urlData = {field: fieldName};
    var data = {};
    var entity;
    var $entityForm = $(".entity-form", $passportSuggestForm);
    entity = $entityForm.data('entity');
    $.extend(data, $entityForm.serializeObject());
    data.entity = entity;
    data[fieldName] = $("#field-value").val();
    settings.data = data;
    return settings;
}

function notifyUpdateResult($elem, message, callback) {
    var $wrapIn = $(".ui.tab.active", $passportUpdate);
    $(".message-text", $elem).html(message);
    $elem.css({
        top: ($wrapIn.height() / 2 - 89) + "px"
    }).one('click', function () {
        $(this).finish();
    })
        .fadeIn('slow').delay(5000).fadeOut('slow', callback);
}

function notifySuggestResult($elem, message, callback) {
    var $wrapIn = $passportSuggestForm;
    $(".message-text", $elem).html(message);
    $elem.css({
        top: ($wrapIn.height() / 2 - 89) + "px"
    }).one('click', function () {
        $(this).finish();
    })
        .fadeIn('slow').delay(5000).fadeOut('slow', callback);
}

function notifyOk(message, callback) {
    notifyUpdateResult($("#new-story-success-message"), message, callback);
}

function notifyError(message, callback) {
    notifyUpdateResult($("#new-story-error-message"), message, callback);
}

function notifySuggestionOk(message, callback) {
    notifySuggestResult($("#suggestion-success-message"), message, callback);
}

function notifySuggestionError(message, callback) {
    notifySuggestResult($("#suggestion-error-message"), message, callback);
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
            .append($("<div class='passport-gallery-control gallery-thumb-arrow-down-overlay' />"))
            .append($("<div class='passport-gallery-control gallery-thumb-arrow-up-overlay' />"))
            .hide().appendTo($passportGallery);

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
            //$passportGalleryControls.app
            $($thumbs[0]).trigger('click');
            // scroll to the first thumb

            if (galldata.length > 0) {
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
    do {
        $(".passport-thumb-image:last", $passportGalleryThumbs)
            .insertBefore($(".passport-thumb-image:first", $passportGalleryThumbs));
    }
    while (!$(".passport-thumb-image", $passportGalleryThumbs).eq(2).hasClass('normal'));
    thumbscroll();
}

function galleryDown() {
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
                $passportCurrentImage.trigger('galleryready');
            } else {
                _debug('street view returned : ' + status);
                pushGalleryThumb({
                    src: '/assets/images/passport/gsv_thumb_inactive.png',
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
