var $pmbottom, $passbtn;

$(document).ready(function () {
    $pmbottom = $("#passportmenu-bottom");
    $passbtn = $('.passport-edit-button');
    $(window).on('resize', fixUI);
    fixUI();
    $(".close-button").on('click', function () {
        $passportWrapper.modal('hide');
    });
    $passportWrapper.modal({
        onHidden: function () {
            removeHash();
        }
    });
    initAddStory();
});

function fixUI() {
    $passbtn.css({left: (($passportWrapper.width() - $passbtn.width()) / 2) + "px"});
}

function fillPassport(church) {
    console.log(church);
    currentChurch = church;
    var churchId = currentChurch.extID;
    $(".church-name").html(church.name);
    $(".church-address").html(church.address.unfolded);
    var website = church.website != null ? church.website : "";
    $(".church-website").html('<a target="_blank" href="' + website + '">' + website + '</a>');
    // fill in new story data
    $("#story-church-id").val(churchId);
    initPassportGallery(churchId);
    initStoryGallery();
}

function initAddStory() {
    $(".submit.button", $newStoryForm).api({
        on: 'click',
        action: 'post new story',
        method: 'POST',
        onSuccess: function (data) {
            console.log(data);
        },
        serializeForm: true
    });
}


function initPassportGallery(id) {
    $passportGalleryWrapper.empty().api({
        action: 'get church images',
        on: 'now',
        urlData: {id: id},
        onSuccess: doPassportGallery
    });
}

var passportGallery;

function doPassportGallery(data) {
    passportGallery = $("<div class='passport-gallery' id='passport_gallery'>");
    passportGallery.appendTo($passportGalleryWrapper);
    $(passportGallery).on('galleryready', function()
    {
        console.log('creating gallery');

    });
    appendGSV();
    $(data).each(function (a, image) {
        $("<img>").attr({src: image.path}).appendTo(passportGallery);
    });
    // append add image thumb
}

function getCoordsString(ll)
{
    return ll[0] + "," + ll[1];
}

function appendGSV() {
    //var gsv = $("<div class='passport-gsv'/>");
    //gsv.appendTo(passportGallery);

    var center = new google.maps.LatLng(currentChurch.address.geometry[0], currentChurch.address.geometry[1]);
    var streetViewService = new google.maps.StreetViewService();
    var maxDistanceFromCenter = 50; //meters
    var galleryHeight = 400;
    streetViewService.getPanoramaByLocation(center, maxDistanceFromCenter, function (streetViewPanoramaData, status) {
        if (status === google.maps.StreetViewStatus.OK) {
            //var coords = getCoordsString(currentChurch.address.geometry);
            var lat = streetViewPanoramaData.location.latLng.lat();
            var lng = streetViewPanoramaData.location.latLng.lng();
            var coords = lat + ',' + lng;
            var heading = getHeading(toRad(lat), toRad(lng), toRad(center.lat()), toRad(center.lng()));
            var url = "https://www.google.com/maps/embed/v1/streetview?location="  + coords + "&key=" + googleApiKey + "&heading=" + heading;
            var gsvElem = "<iframe width='90%' height='" + galleryHeight +
                "' frameborder='0' style='border:0'" +
                " src='"+ url +"'></iframe>";
            console.log(gsvElem);
            $(gsvElem).appendTo(passportGallery);
            passportGallery.trigger('galleryready');
        } else {
            console.log('error calling street view: ' + status);
        }
    });

}

function getHeading(f1, l1, f2, l2)
{
    var y = Math.sin(l2-l1) * Math.cos(f2);
    var x = Math.cos(f1)*Math.sin(f2) -
        Math.sin(f1)*Math.cos(f2)*Math.cos(l2-l1);
    return toDeg(Math.atan2(y, x));
}

function toRad(degree)
{
    return (Math.PI * degree) / 180;
}

function toDeg(rad)
{
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
    $("#story-cover-id", $newStoryForm).val(id);
}