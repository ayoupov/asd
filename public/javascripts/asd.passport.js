var $churchStoriesTitle, $passportUpdateButton, $passportGalleryThumbs,
    $passportUpdate, $newStoryGallery, $passportGallery, $passportWrapper;

var $passportCurrentImage;

$(document).ready(function () {
    $passportWrapper = $(".passport-wrapper");
    $passportGallery = $(".passport-gallery");

    $churchStoriesTitle = $(".passport-stories-title");

    $passportUpdate = $(".passport-update");
    $newStoryGallery = $(".story-cover-gallery");
    $passportUpdateButton = $(".passport-update-button");

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
        .on('click', function(){
            $(this).fadeOut('slow');
        })
        .hide();

    initUpdatePassport();

    // init tabs
    $('.menu .item', $passportUpdate).tab();

    // fill user details once
    if (userHash) {
        $("#user_hash").val(userHash);
        if (userName)
            $("#story-author").val(userName).prop('disabled', true);
        $(".hide-authed").hide();
    } else
    {
        $(".social-login").on('click', function()
        {
            window.location = "/auth/" + $(this).data('provider');
        });
    }
});

var gsvlock;

function fillPassport(church) {
    console.log(church);
    gsvlock = true;
    fillSocial();
    currentChurch = church;
    // clear form
    $("td.passport-value").empty();
    // filling form
    var churchId = currentChurch.extID;
    $(".church-name").html(church.name);
    $(".church-address").html(church.address.unfolded);
    var churchStart = church.constructionStart ? church.constructionStart : "";
    var churchEnd = church.constructionEnd ? church.constructionEnd : "";
    var constructionValue = churchStart == churchEnd ? churchStart : churchStart + " - " + churchEnd;
    $(".church-construction").html(constructionValue);
    var churchArchitects = '';
    var arcarr = [];
    if (church.architects && church.architects.length > 0)
        $(church.architects).each(function (a, item) {
            arcarr.push(item.name);
        });
    churchArchitects = arcarr.join(', ');
    $(".church-architects").html(churchArchitects);
    var website = church.website ? church.website : "";
    if (website != '')
        $(".church-website").html('<a target="_blank" href="' + website + '">' + website + '</a>');
    else
        $(".church-website").html(null);

    // fill in new story data
    $("#story-church-id").val(churchId);
    $passportUpdate.hide();

    $(".passport-update-button-wrapper").off('click').on('click', toggleNewStoryForm);

    // add editable icons and bind events
    $(".editable").each(function (a, item) {
        var content = $(item).html();
        if (!content)
            content = "<div class='passport-data-help'>pomóż nam uzupełnić dane</div>";
        else
            content = "<div class='passport-data-data'>" + content + "</div>";
        $(item).html(content + "<div class='passport-value-edit-icon'/>");
    });
    $(".passport-value-edit-icon, .passport-church-name-edit-icon").on('click', toggleEdit);

    $passportUpdateButton.html("dodaj swoje wspomnienie lub obraz");
    if (church.media && church.media.length > 0) {
        $churchStoriesTitle.html("Wspomnienia dodane przez użytkowników").show();
    }
    else {
        $churchStoriesTitle.hide();
    }

    initPassportGallery(churchId);
    initStoryGallery();
}

function fillSocial() {
    $(".social-wrapper").empty().html(
        "<div class='fb-like' data-href='" + window.location + "#passport' data-layout='button_count' data-action='like'" +
        " data-show-faces='true' data-share='false'></div>" +
        "<a href='https://twitter.com/share' class='twitter-share-button' data-lang='pl'>Tweetnij</a>"
    );
    try {
        if (typeof FB !== 'undefined')
            FB.XFBML.parse();
        if (typeof twttr !== 'undefined')
            twttr.widgets.load();
    } catch (e) {
        console.log('error in social init: ' + e);
    }
}

function toggleNewStoryForm() {
    $(".passport-stories-wrapper").fadeToggle(1000);
    $passportUpdate.fadeToggle(1000);
    $("#story-title,#story-year").val("");
    $("#story-text").html("");
    $passportWrapper.modal('refresh');
}

function toggleEdit(ev) {
    var elem = $(this).parent();
    console.log(elem);
}

function initUpdatePassport() {
    $(".submit.button", $passportUpdate).api({
        on: 'click',
        action: 'update passport',
        method: 'POST',
        onSuccess: function (data) {
            console.log(data);
            toggleNewStoryForm();
            notifyOk();
        },
        // onFailure ??
        onError: function (errorMessage) {
            //toggleNewStoryForm();
            notifyError(errorMessage);
        },
        beforeSend: function(settings){ return gather(settings, $(this));}
    });
}

function gather(settings, $source)
{
    var data = {};
    // sending field or {new story|images}
    var fieldOnly = $source.hasClass('passport-field-update-button');
    var entity;
    if (fieldOnly)
    {
        entity = 'field';
        // todo: add fieldname and value to data
    } else
    {
        var $entityForm = $(".ui.tab.active form", $passportUpdate);
        entity = $entityForm.data('entity');
        $.extend(data, $entityForm.serializeObject());
        $.extend(data, $('.common-form').serializeObject());
    }
    data.entity = entity;
    settings.data = data;
    return settings;
}

function notifyOk() {
    $("#new-story-success-message").fadeIn('slow');
}

function notifyError() {
    $("#new-story-error-message").fadeIn('slow');
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
        console.log('gallery ready');
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
                console.log(gsvElem);
                $(gsvElem).appendTo($passportCurrentImage);
                var $gsvThumbElem = $('<img>').attr({src: '/assets/uploads/000000/gsv_thumb.png'})
                    .data('big-src', '#gsv-special');
                pushGalleryThumb($gsvThumbElem);
                $passportCurrentImage.trigger('galleryready');
            } else {
                console.log('street view returned : ' + status);
                $passportCurrentImage.trigger('galleryready');
            }
        });
    gsvlock = false;
}

function getHeading(f1, l1, f2, l2) {
    var y = Math.sin(l2 - l1) * Math.cos(f2);
    var x = Math.cos(f1) * Math.sin(f2) -
        Math.sin(f1) * Math.cos(f2) * Math.cos(l2 - l1);
    return toDeg(Math.atan2(y, x));
}

function toRad(degree) {
    return (Math.PI * degree) / 180;
}

function toDeg(rad) {
    return rad * 180 / Math.PI;
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