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


function initPassportGallery(id)
{
    $passportGalleryWrapper.empty().api({
        action: 'get church images',
        on: 'now',
        urlData: {id : id},
        onSuccess: doPassportGallery
    });
}

var DEFAULT_PASSPORT_GALL_OPTS =
{
    gallery_autoplay: false,
    gallery_carousel: true,
    gallery_debug_errors: false
};

function doPassportGallery(data)
{
    var passportGallery = $("<div class='passport-gallery'>");
    appendGSV(passportGallery);

    passportGallery.appendTo($passportGalleryWrapper);
}

function appendGSV(where)
{
   console.log(currentChurch.address.coordinates);
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
    // todo: visualize selection???
    $("#story-cover-id", $newStoryForm).val(id);
}